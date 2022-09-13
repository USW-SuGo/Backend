package com.usw.sugo.domain.majoruser.user.controller;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsEmailExistResponse;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.user.service.UserService;
import com.usw.sugo.domain.majoruser.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.majoruser.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.global.jwt.JwtUtilizer;
import com.usw.sugo.global.util.ses.SendEmailServiceFromSES;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.usw.sugo.exception.UserErrorCode.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final JwtUtilizer jwtUtilizer;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserService userService;
    private final UserRepository userRepository;
    private final SendEmailServiceFromSES sendEmailServiceFromSES;

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

        Optional<User> requestUser = userRepository.findByEmail(sendAuthorizationEmailRequest.getEmail());

        // 인증 메일을 보낼 메일 주소가, 이미 존재할 때 Error
        if (requestUser.isPresent()) {
            throw new CustomException(DUPLICATED_EMAIL);
        }

        // 신규 유저인 경우, Email DB 에 저장 후 엔티티 반환
        User newUser = userService.softSaveUser(sendAuthorizationEmailRequest.getEmail());

        // 이메일 토큰 생성 및 DB 저장
        String authPayload = "http://localhost:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());
        //String authPayload = "https://api.sugo:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());

        // 이메일 발송
        sendEmailServiceFromSES.send(sendAuthorizationEmailRequest.getEmail(), authPayload);

        // 반환
        Map<String, Boolean> result = new HashMap<>() {{put("Success", true);}};

        return ResponseEntity.status(OK).body(result);
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("/verify-authorization-email")
    public String ConfirmEmail(@RequestParam("auth") String payload) {

        Optional<UserEmailAuth> requestUser = userEmailAuthRepository.findByPayload(payload);

        // 이메일 인증을 요청한 사용자가 DB 에 없으면 에러
        if (requestUser.isEmpty()) throw new CustomException(INVALID_AUTH_TOKEN);

        // 이메일 인증 로직
        userEmailAuthService.authorizeToken(payload);

        // 유저 Status 컬럼 수정 -> Available
        userRepository.authorizeToken(requestUser.get().getUserId());

        return "인증에 성공";
    }

    @PostMapping("/detail-join")
    public ResponseEntity<?> detailJoin(@RequestBody DetailJoinRequest detailJoinRequest) {

        Optional<User> requestUser = userRepository.findByEmail(detailJoinRequest.getEmail());

        if (requestUser.isPresent()) {
            // 유저 인덱스
            Long userId = requestUser.get().getId();
            // 비밀번호 암호화
            // 닉네임 발급
            // -> 최종 회원가입 처리
            userRepository.detailJoin(detailJoinRequest, userId);
            // 유저 변경 시각 타임스탬프
            userRepository.setModifiedDate(userId);
        }
        // 반환
        Map<String, Boolean> result = new HashMap<>() {{put("Success", true);}};
        return ResponseEntity.status(OK).body(result);
    }

    /**
     로그인 컨트롤러
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> requestUser = userRepository.findByEmail(loginRequest.getEmail());

        if (requestUser.isEmpty()) throw new CustomException(USER_NOT_EXIST);

        User notWrappedRequestUser = requestUser.get();
        Map<String, String> result = new HashMap<>(2);

        // 비밀번호가 일치하면
        if (userService.matchingPassword(loginRequest.getPassword(), notWrappedRequestUser)) {
            // 토큰 갱신
            if (refreshTokenRepository.findByUserId(notWrappedRequestUser.getId()).isPresent()) {
                result.put("AccessToken", jwtUtilizer.createAccessToken(notWrappedRequestUser));
                result.put("RefreshToken", jwtUtilizer.refreshRefreshToken(notWrappedRequestUser));
            } 
            // 토큰 신규 생성
            else if (refreshTokenRepository.findByUserId(notWrappedRequestUser.getId()).isEmpty()) {
                result.put("AccessToken", jwtUtilizer.createAccessToken(notWrappedRequestUser));
                result.put("RefreshToken", jwtUtilizer.createRefreshToken(notWrappedRequestUser));
            }
        }
        // 비밀번호가 일치하지 않으면 에러 터뜨리기
        else if (!userService.matchingPassword(loginRequest.getPassword(), requestUser.get())) {
            throw new CustomException(PASSWORD_NOT_CORRECT);
        }
        return ResponseEntity.status(OK).body(result);
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<?> editPassword(@RequestBody EditPasswordRequest editPasswordRequest) {

        if (userService.isSamePassword(editPasswordRequest.getId(), editPasswordRequest.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }

        // 비밀번호 수정
        userRepository.editPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword());
        // 유저 변경 시각 타임스탬프
        userRepository.setModifiedDate(editPasswordRequest.getId());
        //반환
        Map<String, Boolean> result = new HashMap<>() {{
            put("Success", true);
        }};
        return ResponseEntity.status(OK).body(result);
    }
}
