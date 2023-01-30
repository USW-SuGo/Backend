package com.usw.sugo.domain.user.controller;

import com.usw.sugo.domain.emailauth.UserEmailAuth;
import com.usw.sugo.domain.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.notefile.service.NoteFileService;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.dto.UserRequestDto;
import com.usw.sugo.domain.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.domain.user.service.UserService;
import com.usw.sugo.domain.userlikepost.repository.UserLikePostRepository;
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
            @Valid @RequestBody UserRequestDto.IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        userService.validateLoginIdDuplicated(isLoginIdExistRequestForm.getLoginId());
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("exist", false);
                }});
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Valid @RequestBody UserRequestDto.IsEmailExistRequestForm isEmailExistRequestForm) {
        userService.validateSuwonAcKrEmail(isEmailExistRequestForm.getEmail());
        userService.validateEmailDuplicated(isEmailExistRequestForm.getEmail());
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("exist", false);
                }});
    }

    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Boolean>> findId(@Valid @RequestBody UserRequestDto.FindLoginIdRequestForm findLoginIdRequestForm) {
        User requestUser = userRepository.findByEmail(
                        findLoginIdRequestForm.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        sendEmailServiceBySES.sendFindLoginIdResult(findLoginIdRequestForm.getEmail(), requestUser.getLoginId());
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, Boolean>> sendPasswordEmail(
            @Valid @RequestBody UserRequestDto.FindPasswordRequestForm findPasswordRequestForm) {
        User requestUser = userRepository.findByEmail(findPasswordRequestForm.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        String newPassword = userService.initPassword(requestUser.getId());
        sendEmailServiceBySES.sendFindPasswordResult(findPasswordRequestForm.getEmail(), newPassword);
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> detailJoin(
            @Valid @RequestBody UserRequestDto.DetailJoinRequestForm detailJoinRequestForm) {
        userService.validateLoginIdDuplicated(detailJoinRequestForm.getLoginId());
        userService.validateSuwonAcKrEmail(detailJoinRequestForm.getEmail());
        userService.validateEmailDuplicated(detailJoinRequestForm.getEmail());
        User requestUser = userService.softJoin(detailJoinRequestForm);
        userRepository.editNickname(
                requestUser.getId(),
                nicknameGenerator.generateNickname(detailJoinRequestForm.getDepartment()));
        String authPayload = userEmailAuthService.createEmailAuthPayload(requestUser.getId());
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
            put("id", requestUser.getId());
        }});
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Boolean>> confirmEmail(@RequestBody UserRequestDto.AuthEmailPayloadForm authEmailPayloadForm) {
        String payload = authEmailPayloadForm.getPayload();
        UserEmailAuth requestUserEmailAuth = userEmailAuthRepository
                .findByUserId(authEmailPayloadForm.getUserId())
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
            @Valid @RequestBody UserRequestDto.EditPasswordRequestForm editPasswordRequestForm) {
        if (userService.matchingPassword(editPasswordRequestForm.getId(), editPasswordRequestForm.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }

        userRepository.editPassword(editPasswordRequestForm.getId(), editPasswordRequestForm.getPassword());
        userRepository.setModifiedDate(editPasswordRequestForm.getId());

        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteUser(
            @RequestHeader String authorization, @Valid @RequestBody UserRequestDto.QuitRequestForm quitRequestForm) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        userService.isUserExistByLoginId(quitRequestForm.getLoginId());

        if (!userService.matchingPassword(requestUserId, quitRequestForm.getPassword()))
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
            @RequestHeader String authorization, @Valid @RequestBody UserRequestDto.MannerEvaluationRequestForm mannerEvaluationRequestForm) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        userService.isUserExistByUserId(requestUserId);
        userService.isUserExistByUserId(mannerEvaluationRequestForm.getTargetUserId());
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        if (userService.isBeforeDay(requestUser.getRecentEvaluationManner())) {
            userRepository.setRecentMannerGradeDate(
                    mannerEvaluationRequestForm.getGrade(), mannerEvaluationRequestForm.getTargetUserId(), requestUserId);
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }

        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{
            put("Success", true);
        }});
    }
}
