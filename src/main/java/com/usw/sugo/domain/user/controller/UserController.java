package com.usw.sugo.domain.user.controller;

import com.usw.sugo.domain.notefile.service.NoteFileService;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.dto.UserRequestDto;
import com.usw.sugo.domain.user.dto.UserResponseDto;
import com.usw.sugo.domain.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.domain.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.emailauth.UserEmailAuth;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.domain.user.service.UserService;
import com.usw.sugo.global.aws.ses.SendEmailServiceBySES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ExceptionType.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final JwtResolver jwtResolver;
    private final UserService userService;
    private final NoteFileService noteFileService;
    private final NicknameGenerator nicknameGenerator;
    private final UserRepository userRepository;
    private final UserLikePostRepository userLikePostRepository;
    private final ProductPostRepository productPostRepository;
    private final SendEmailServiceBySES sendEmailServiceBySES;
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final UserEmailAuthService userEmailAuthService;

    @PostMapping("/check-loginId")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(
            @Valid @RequestBody UserRequestDto.IsLoginIdExistRequest isLoginIdExistRequest) {
        userService.validateLoginIdDuplicated(isLoginIdExistRequest.getLoginId());
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("exist", false);
                }});
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Valid @RequestBody UserRequestDto.IsEmailExistRequest isEmailExistRequest) {
        userService.validateSuwonAcKrEmail(isEmailExistRequest.getEmail());
        userService.validateEmailDuplicated(isEmailExistRequest.getEmail());
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("exist", false);
                }});
    }

    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Boolean>> findId(@Valid @RequestBody UserRequestDto.FindLoginIdRequest findLoginIdRequest) {
        User requestUser = userRepository.findByEmail(
                        findLoginIdRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        sendEmailServiceBySES.sendFindLoginIdResult(findLoginIdRequest.getEmail(), requestUser.getLoginId());
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, Boolean>> sendPasswordEmail(
            @Valid @RequestBody UserRequestDto.FindPasswordRequest findPasswordRequest) {
        User requestUser = userRepository.findByEmail(findPasswordRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        String newPassword = userService.initPassword(requestUser.getId());
        sendEmailServiceBySES.sendFindPasswordResult(findPasswordRequest.getEmail(), newPassword);
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> detailJoin(
            @Valid @RequestBody UserRequestDto.DetailJoinRequest detailJoinRequest) {
        userService.validateLoginIdDuplicated(detailJoinRequest.getLoginId());
        userService.validateSuwonAcKrEmail(detailJoinRequest.getEmail());
        userService.validateEmailDuplicated(detailJoinRequest.getEmail());
        User requestUser = userService.softJoin(detailJoinRequest);
        userRepository.editNickname(
                requestUser.getId(),
                nicknameGenerator.generateNickname(detailJoinRequest.getDepartment()));
        String authPayload = userEmailAuthService.createEmailAuthPayload(requestUser.getId());
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
            put("id", requestUser.getId());
        }});
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Boolean>> confirmEmail(@RequestBody UserRequestDto.AuthEmailPayload authEmailPayload) {
        String payload = authEmailPayload.getPayload();
        UserEmailAuth requestUserEmailAuth = userEmailAuthRepository
                .findByUserId(authEmailPayload.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_SEND_AUTH_EMAIL));

        if (requestUserEmailAuth.getPayload().equals(payload)) {
            User requestUser = requestUserEmailAuth.getUser();
            userEmailAuthService.authorizeEmailByPayload(payload);
            userRepository.passwordEncode(requestUser, requestUser.getId());
            userRepository.modifyingStatusToAvailable(requestUser.getId());
            userEmailAuthRepository.deleteByUser(requestUser);
            return ResponseEntity.status(OK).body(new HashMap<>() {{
                put("Success", true);
            }});
        }
        throw new CustomException(PAYLOAD_NOT_VALID);
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<Map<String, Boolean>> editPassword(
            @Valid @RequestBody UserRequestDto.EditPasswordRequest editPasswordRequest) {
        if (userService.matchingPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }

        userRepository.editPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword());
        userRepository.setModifiedDate(editPasswordRequest.getId());

        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteUser(
            @RequestHeader String authorization, @Valid @RequestBody UserRequestDto.QuitRequest quitRequest) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        userService.isUserExistByLoginId(quitRequest.getLoginId());

        if (!userService.matchingPassword(requestUserId, quitRequest.getPassword()))
            throw new CustomException(PASSWORD_NOT_CORRECT);

        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        noteFileService.deleteNoteFile(requestUser);
        userRepository.deleteById(requestUserId);

        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @GetMapping("/identifier")
    public ResponseEntity<Map<String, Long>> getMyIndex(@RequestHeader String authorization) {
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("userId", jwtResolver.jwtResolveToUserId(authorization.substring(7)));
                }});

    }

    @GetMapping
    public ResponseEntity<UserPageResponseForm> loadMyPage(@RequestHeader String authorization, Pageable pageable) {
        long targetUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        User requestUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        UserPageResponseForm userPageResponseForm1 = userRepository.loadUserPage(requestUser).builder()
                .myPosting(productPostRepository.loadUserWritingPostingList(requestUser, pageable))
                .likePosting(userLikePostRepository.loadMyLikePosting(requestUser.getId()))
                .build();

        UserPageResponseForm userPageResponseForm = userRepository.loadUserPage(requestUser);
        return ResponseEntity
                .status(OK)
                .body(userPageResponseForm);
    }

    @GetMapping("/")
    public ResponseEntity<UserPageResponseForm> otherUserPage(@RequestParam long userId, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        UserPageResponseForm targetUserPageResponseForm = userRepository.loadUserPage(targetUser).builder()
                .myPosting(productPostRepository.loadUserWritingPostingList(targetUser, pageable))
                .build();
        return ResponseEntity
                .status(OK)
                .body(targetUserPageResponseForm);
    }

    @PostMapping("/manner")
    public ResponseEntity<?> evaluateManner(
            @RequestHeader String authorization, @Valid @RequestBody UserRequestDto.MannerEvaluationRequest mannerEvaluationRequest) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        userService.isUserExistByUserId(requestUserId);
        userService.isUserExistByUserId(mannerEvaluationRequest.getTargetUserId());
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        if (userService.isBeforeDay(requestUser.getRecentEvaluationManner())) {
            userRepository.setRecentMannerGradeDate(
                    mannerEvaluationRequest.getGrade(), mannerEvaluationRequest.getTargetUserId(), requestUserId);
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }

        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{
            put("Success", true);
        }});
    }
}
