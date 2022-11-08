package com.usw.sugo.domain.user.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UserRequestDto {

    // 이메일 중복확인 DTO
    @Data
    public static class IsEmailExistRequest {
        @NotNull @NotBlank
        private String email;
    }

    // 아이디 중복확인 DTO
    @Data
    public static class IsLoginIdExistRequest {
        @NotNull @NotBlank
        private String loginId;
    }

    // 회원가입 DTO
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

    // 인증번호 DTO
    @Data
    public static class AuthEmailPayload {
        @NotNull @NotBlank
        private long userId;
        @NotNull @NotBlank
        private String payload;
    }

    // 로그인 아이디 찾기 DTO
    @Data
    public static class FindLoginIdRequest {
        @NotNull @NotBlank
        private String email;
    }

    // 비밀번호 찾기 DTO
    @Data
    public static class FindPasswordRequest {
        @NotNull @NotBlank
        private String loginId;
        @NotNull @NotBlank
        private String email;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequest {
        @NotNull @NotBlank
        private String loginId;
        @NotNull @NotBlank
        private String password;
    }

    // 비밀번호 수정 DTO
    @Data
    public static class EditPasswordRequest {
        @NotNull @NotBlank
        private long id;
        @NotNull @NotBlank
        private String password;
    }

    // 회원탈퇴 요청 DTO
    @Data
    public static class QuitRequest {
        @NotNull @NotBlank
        private String loginId;
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
