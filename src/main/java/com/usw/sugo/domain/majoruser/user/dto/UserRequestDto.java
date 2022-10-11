package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequest {
        @NotNull
        private String email;
    }

    @Data
    public static class IsLoginIdExistRequest {
        @NotNull
        private String loginId;
    }

    @Data
    public static class SendAuthorizationEmailRequest {
        @NotNull
        private String email;
    }
    @Data
    public static class FindLoginIdRequest {
        @NotNull
        private String email;
    }

    @Data
    public static class SendPasswordRequest {
        @NotNull
        private String loginId;
        @NotNull
        private String email;
    }

    // 인증받은 이메일과 비밀번호를 입력받기
    // 부가 정보 입력받는 2차 회원가입 요청 DTO
    @Data
    public static class DetailJoinRequest {
        @NotNull
        private String email;
        @NotNull
        private String loginId;
        @NotNull
        private String password;
        @NotNull
        private String department;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequest {
        @NotNull
        private String loginId;
        @NotNull
        private String password;
    }

    @Data
    public static class EditPasswordRequest {
        @NotNull
        private Long id;
        @NotNull
        private String password;
    }

    // 회원탈퇴 요청
    @Data
    public static class QuitRequest {
        @NotNull
        private String email;
        @NotNull
        private String password;
    }

    @Data
    public static class MannerEvaluationRequest {
        @NotNull
        private long targetUserId;
        @NotNull
        private BigDecimal grade;
    }
}
