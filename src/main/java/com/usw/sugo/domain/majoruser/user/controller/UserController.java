package com.usw.sugo.domain.majoruser.user.controller;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsEmailExistResponse;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.user.service.UserService;
import com.usw.sugo.domain.majoruser.useremailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.majoruser.useremailauth.service.UserEmailAuthService;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.ErrorCode;
import com.usw.sugo.global.util.ses.SendEmailServiceFromSES;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.usw.sugo.exception.ErrorCode.DUPLICATED_EMAIL;
import static com.usw.sugo.exception.ErrorCode.INVALID_AUTH_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SendEmailServiceFromSES sendEmailServiceFromSES;

    private final UserEmailAuthRepository userEmailAuthRepository;

    private final UserEmailAuthService userEmailAuthService;

    // 이메일 중복 확인
    @PostMapping("/check-email")
    public ResponseEntity<IsEmailExistResponse> checkEmail(@RequestBody IsEmailExistRequest isEmailExistRequest) {

        IsEmailExistResponse isEmailExistResponse = new IsEmailExistResponse(false);

        if (userRepository.findByEmail(isEmailExistRequest.getEmail()).isPresent()) {
            isEmailExistResponse.setExist(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(isEmailExistResponse);
    }

    // 재학생 인증 이메일 전송하기
    @PostMapping("/send-authorization-email")
    public ResponseEntity<?> sendAuthorizationEmail(
            @RequestBody SendAuthorizationEmailRequest sendAuthorizationEmailRequest) {

        Optional<User> requestUser = userRepository.findByEmail(sendAuthorizationEmailRequest.getEmail());

        // 인증 메일을 보낼 메일 주소가, 이미 존재할 때 Error
        if (requestUser.isPresent()) {
            throw new CustomException(DUPLICATED_EMAIL);
        }

        // 인증 메일을 보낼 메일 주소가, DB에 없을 때
        else if (requestUser.isEmpty()) {
            // 신규 유저인 경우, Email DB 에 저장 후 엔티티 반환
            User newUser = userService.softSaveUser(sendAuthorizationEmailRequest.getEmail());

            // 이메일 토큰 생성 및 DB 저장
            String authPayload = "http://localhost:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());
            //String authPayload = "https://api.sugo:8080/user/verify-authorization-email?auth=" + userEmailAuthService.createEmailAuthToken(newUser.getId());

            // 이메일 발송
            sendEmailServiceFromSES.send(sendAuthorizationEmailRequest.getEmail(), authPayload);
        }
        Map<String, Boolean> result = new HashMap<>() {{put("Success", true);}};

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("/verify-authorization-email")
    public String ConfirmEmail(@RequestParam("auth") String payload) {

        Optional<UserEmailAuth> requestUser = userEmailAuthRepository.findByPayload(payload);

        // 이메일 인증을
        if (requestUser.isEmpty()) throw new CustomException(INVALID_AUTH_TOKEN);

        // 이메일 인증 로직
        userEmailAuthService.authorizeToken(payload);

        // 유저 Status 컬럼 수정 -> Available
        userRepository.authorizeToken(requestUser.get().getUserId());

        return "인증에 성공";
    }

    @PostMapping("/detail-join")
    public ResponseEntity<?> detailJoin(@RequestBody DetailJoinRequest detailJoinRequest) throws Exception {
        Map<String, Boolean> result = new HashMap<>() {{put("Success", true);}};
        Optional<User> requestUser = userRepository.findByEmail(detailJoinRequest.getEmail());

        // 비밀번호 암호화 및 닉네임 발급 -> 최종 회원가입 처리
        if (requestUser.isPresent()) {
            Long userId = requestUser.get().getId();
            userRepository.detailJoin(detailJoinRequest, userId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<?> editPassword(@RequestBody EditPasswordRequest editPasswordRequest) {
        Map<String, Boolean> result = new HashMap<>();

        // 비밀번호 수정
        userRepository.editPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 닉네임 수정
    @PutMapping("/nickname")
    public ResponseEntity<?> editNickname(@RequestBody EditNicknameRequest editNicknameRequest) {
        Map<String, Boolean> result = new HashMap<>();

        userRepository.editNickname(editNicknameRequest.getId(), editNicknameRequest.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
