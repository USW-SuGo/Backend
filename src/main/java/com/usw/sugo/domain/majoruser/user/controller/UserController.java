package com.usw.sugo.domain.majoruser.user.controller;

import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.majoruser.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsEmailExistResponse;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.UserPageResponse;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.user.service.UserService;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.domain.status.Status;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.aws.ses.AuthSuccessViewForm;
import com.usw.sugo.global.aws.ses.SendEmailServiceFromSES;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final JwtGenerator jwtGenerator;
    private final JwtResolver jwtResolver;
    private final JwtValidator jwtValidator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    private final ProductPostRepository productPostRepository;

    private final SendEmailServiceFromSES sendEmailServiceFromSES;

    private final AuthSuccessViewForm authSuccessViewForm;

    private final UserEmailAuthRepository userEmailAuthRepository;

    private final UserEmailAuthService userEmailAuthService;

    // 이메일 중복 확인
    @PostMapping("/check-email")
    public ResponseEntity<IsEmailExistResponse> checkEmail(@RequestBody IsEmailExistRequest isEmailExistRequest) {

        IsEmailExistResponse isEmailExistResponse = new IsEmailExistResponse(false);

        // 이메일이 DB에 존재하면
        if (userRepository.findByEmail(isEmailExistRequest.getEmail()).isPresent()) {
            isEmailExistResponse.setExist(true);
        }

        return ResponseEntity.status(OK).body(isEmailExistResponse);
    }

    // 재학생 인증 이메일 전송하기
    @PostMapping("/send-authorization-email")
    public ResponseEntity<Map<String, Boolean>> sendAuthorizationEmail(
            @RequestBody SendAuthorizationEmailRequest sendAuthorizationEmailRequest) {

        // 이메일 중복 시 에러
        userRepository.findByEmail(sendAuthorizationEmailRequest.getEmail())
                .orElseThrow(() -> new CustomException(DUPLICATED_EMAIL));

        // 신규 유저인 경우, Email DB 에 저장 후 엔티티 반환
        User newUser = userService.softSaveUser(sendAuthorizationEmailRequest.getEmail());

        // 이메일 토큰 생성 및 DB 저장
        String authPayload = "http://localhost:8080/user/verify-authorization-email?auth=" +
                userEmailAuthService.createEmailAuthToken(newUser.getId());
//        String authPayload = "https://api.sugo:8080/user/verify-authorization-email?auth=" +
//                userEmailAuthService.createEmailAuthToken(newUser.getId());

        // 이메일 발송
        sendEmailServiceFromSES.send(sendAuthorizationEmailRequest.getEmail(), authPayload);

        // 반환
        Map<String, Boolean> result = new HashMap<>() {{
            put("Success", true);
        }};

        return ResponseEntity.status(OK).body(result);
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("/verify-authorization-email")
    public String ConfirmEmail(@RequestParam("auth") String payload) {

        // 이메일 인증을 요청한 사용자가 DB 에 없으면 에러
        UserEmailAuth requestUser = userEmailAuthRepository.findByPayload(payload)
                .orElseThrow(() -> new CustomException(INVALID_AUTH_TOKEN));

        // 이메일 인증 로직
        userEmailAuthService.authorizeToken(payload);

        // 유저 Status 컬럼 수정 -> Available
        userRepository.modifyingStatusToAvailable(requestUser.getUser().getId());

        return authSuccessViewForm.successParagraph();
    }

    @PostMapping("/join")
    public ResponseEntity<?> detailJoin(@RequestBody DetailJoinRequest detailJoinRequest) {

        // DB에 존재하지 않는 유저일 때 에러
        User requestUser = userRepository.findByEmail(detailJoinRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        // 이미 회원가입을 수행한 유저일 때
        if (requestUser.getNickname() != null) {
            throw new CustomException(USER_ALREADY_JOIN);
        }

        // User 가 DB에 존재하고, 이메일 인증을 받았을 때
        if (requestUser.getStatus().equals(Status.AVAILABLE)) {
            Long userId = requestUser.getId();

            // 비밀번호 암호화, 닉네임 발급 -> 최종 회원가입 처리
            userRepository.detailJoin(detailJoinRequest, userId);
            // 유저 변경 시각 타임스탬프
            userRepository.setModifiedDate(userId);
        }
        // 이메일 인증은 아직 안받았을 때
        else if (!requestUser.getStatus().equals(Status.AVAILABLE)) {
            throw new CustomException(NOT_AUTHORIZED_EMAIL);
        }

        // 반환
        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{
            put("Success", true);
        }});
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<?> editPassword(
            @RequestHeader String authorization,
            @RequestBody EditPasswordRequest editPasswordRequest) {

        // 이전 비밀번호와 같은 내용으로 변경하려 할 때
        if (userService.matchingPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }

        // 비밀번호 수정
        userRepository.editPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword());
        // 유저 변경 시각 타임스탬프
        userRepository.setModifiedDate(editPasswordRequest.getId());
        //반환

        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{put("Success", true);}});
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader String authorization, @RequestBody QuitRequest quitRequest) {

        Long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(6));

        // DB에 없는 유저면 에러
        userRepository.findByEmail(quitRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        // 비밀번호가 일치하지 않을 때
        if (!userService.matchingPassword(requestUserId, quitRequest.getPassword())) {
            throw new CustomException(PASSWORD_NOT_CORRECT);
        }

        // 비밀번호 수정
        userRepository.deleteById(requestUserId);

        //반환
        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{put("Success", true);}});
    }

    // 유저 페이지
    @GetMapping("/")
    public ResponseEntity<UserPageResponse> userPage(@RequestHeader String authorization,
                                                     @RequestParam @Nullable Long target, Pageable pageable) {

        UserPageResponse userPageResponse = new UserPageResponse();

        // 파라미터 값을 안넣었을 때 (마이페이지 요청)
        if (target == null) {
            long targetUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
            userRepository.findById(targetUserId).orElseThrow(() -> new CustomException(USER_NOT_EXIST));

            User targetUserIsMe = userRepository.findById(targetUserId).get();
            userPageResponse = UserPageResponse.builder()
                    .userId(targetUserId)
                    .email(targetUserIsMe.getEmail())
                    .nickname(targetUserIsMe.getNickname())
                    .mannerGrade(targetUserIsMe.getMannerGrade())
                    .myPosting(productPostRepository.loadUserPageList(targetUserIsMe, pageable))
                    .build();

        }
        // 파라미터 값을 넣었을 때 (다른 유저의 마이페이지)
        else if (target != null) {
            userRepository.findById(target).orElseThrow(() -> new CustomException(USER_NOT_EXIST));

            User targetUser = userRepository.findById(target).get();
            userPageResponse = UserPageResponse.builder()
                    .userId(target)
                    .nickname(targetUser.getNickname())
                    .email(targetUser.getEmail())
                    .mannerGrade(targetUser.getMannerGrade())
                    .myPosting(productPostRepository.loadUserPageList(targetUser, pageable))
                    .build();
        }
        return ResponseEntity.status(200).body(userPageResponse);
    }

    @PostMapping("/manner")
    public ResponseEntity<?> userPage(@RequestHeader String authorization,
                                      @RequestBody MannerEvaluationRequest mannerEvaluationRequest) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        // 평가를 요청한 유저가 존재하지 않으면 에러
        userRepository.findById(requestUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        // 평가 대상 유저가 존재하지 않으면 에러
        userRepository.findById(mannerEvaluationRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        User requestUser = userRepository.findById(requestUserId).get();

        // 매너 평가한지 하루가 지나지 않았을 때
        if (userService.isBeforeDay(requestUser.getRecentEvaluationManner())) {
            userRepository.setRecentMannerGradeDate(
                    mannerEvaluationRequest.getGrade(), mannerEvaluationRequest.getTargetUserId(), requestUserId);
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }

        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{put("Success", true);}});
    }
}
