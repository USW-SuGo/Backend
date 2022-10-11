package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequest {
        @NotNull @NotBlank
        private String email;
    }

    @Data
    public static class IsLoginIdExistRequest {
        @NotNull @NotBlank
        private String loginId;
    }

    @Data
    public static class SendAuthorizationEmailRequest {
        @NotNull @NotBlank
        private String email;
    }
    @Data
    public static class FindLoginIdRequest {
        @NotNull @NotBlank
        private String email;
    }

    @Data
    public static class SendPasswordRequest {
        @NotNull @NotBlank
        private String loginId;
        @NotNull @NotBlank
        private String email;
    }

    // 인증받은 이메일과 비밀번호를 입력받기
    // 부가 정보 입력받는 2차 회원가입 요청 DTO
    @Data
    public static class DetailJoinRequest {
        @NotNull @NotBlank
        private String email;
        @NotNull @NotBlank
        private String loginId;
        @NotNull @NotBlank
        private String password;
        @NotNull @NotBlank
        private String department;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequest {
        @NotNull @NotBlank
        private String loginId;
        @NotNull @NotBlank
        private String password;
    }

    @Data
    public static class EditPasswordRequest {
        @NotNull @NotBlank
        private Long id;
        @NotNull @NotBlank
        private String password;
    }

    // 회원탈퇴 요청
    @Data
    public static class QuitRequest {
        @NotNull @NotBlank
        private String email;
        @NotNull @NotBlank
        private String password;
    }

    @Data
    public static class MannerEvaluationRequest {
        @NotNull @NotBlank
        private long targetUserId;
        @NotNull @NotBlank
        private BigDecimal grade;
    }
}
