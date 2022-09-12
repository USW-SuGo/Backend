package com.usw.sugo.domain.majoruser.user.controller;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.IsEmailExistRequest;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.SendAuthorizationEmailRequest;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsEmailExistResponse;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.user.service.UserService;
import com.usw.sugo.domain.majoruser.useremailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.majoruser.useremailauth.service.UserEmailAuthService;
import com.usw.sugo.global.util.ses.SendEmailServiceFromSES;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SendEmailServiceFromSES sendEmailServiceFromSES;
    
    private final UserEmailAuthRepository userEmailAuthRepository;

    private final UserEmailAuthService userEmailAuthService;

    @PostMapping("/check-email")
    public ResponseEntity<IsEmailExistResponse> checkEmail(@RequestBody IsEmailExistRequest isEmailExistRequest) {

        IsEmailExistResponse isEmailExistResponse = new IsEmailExistResponse(false);

        if (userRepository.findByEmail(isEmailExistRequest.getEmail()).isPresent()) {
            isEmailExistResponse.setExist(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(isEmailExistResponse);
    }

    @PostMapping("/send-authorization-email")
    public ResponseEntity<?> sendAuthorizationEmail(@RequestBody SendAuthorizationEmailRequest sendAuthorizationEmailRequest) throws Exception {

        Map<String, Boolean> result = new HashMap<>();

        Optional<User> requestUser = userRepository.findByEmail(sendAuthorizationEmailRequest.getEmail());

        if (requestUser.isPresent()) {
            throw new Exception("이미 인증을 요청한 이메일 입니다. 메일 수신함을 확인해주세요");
        }

        else if (requestUser.isEmpty()) {
            // 신규 유저인 경우, Email DB 에 저장 후 엔티티 반환
            User newUser = userService.softSaveUser(sendAuthorizationEmailRequest.getEmail());

            // 이메일 토큰 생성 및 DB 저장
            String authPayload = "http://localhost:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());
            //String authPayload = "https://api.sugo:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());

            // 이메일 발송
            sendEmailServiceFromSES.send(sendAuthorizationEmailRequest.getEmail(), authPayload);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("/verify-authorization-email")
    public String ConfirmEmail(@RequestParam("auth") String payload) throws Exception {
        Optional<UserEmailAuth> requestUser = userEmailAuthRepository.findByPayload(payload);

        if (requestUser.isEmpty()) {
            throw new Exception("존재하지 않는 토큰임");
        }

        userEmailAuthService.authorizeToken(payload);

        return "인증에 성공";
    }
}
