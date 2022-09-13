package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequest {
        String email;
    }

    @Data
    public static class SendAuthorizationEmailRequest {
        String email;
    }

    // 인증받은 이메일과 비밀번호를 입력받기
    // 부가 정보 입력받는 2차 회원가입 요청 DTO
    @Data
    public static class DetailJoinRequest {
        String email;
        String password;
        String department;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequest {
        String email;
        String password;
    }

    @Data
    public static class EditPasswordRequest {
        Long id;
        String password;
    }

}
