package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;

import java.math.BigDecimal;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequest {
        private String email;
    }

    @Data
    public static class SendAuthorizationEmailRequest {
        private String email;
    }

    // 인증받은 이메일과 비밀번호를 입력받기
    // 부가 정보 입력받는 2차 회원가입 요청 DTO
    @Data
    public static class DetailJoinRequest {
        private String email;
        private String password;
        private String department;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class EditPasswordRequest {
        private Long id;
        private String password;
    }

    // 회원탈퇴 요청
    @Data
    public static class QuitRequest {
        private String email;
        private String password;
    }

    @Data
    public static class MannerEvaluationRequest {
        private long targetUserId;
        private BigDecimal grade;
    }
}
