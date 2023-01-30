package com.usw.sugo.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

// NotBlank는 String 타입에서만 가능
// Integer, Long 은 Notnull가능
public class UserRequestDto {

    // 이메일 중복확인 DTO
    @Data
    public static class IsEmailExistRequestForm {
        @NotNull
        @NotBlank
        private String email;
    }

    // 아이디 중복확인 DTO
    @Data
    public static class IsLoginIdExistRequestForm {
        @NotNull
        @NotBlank
        private String loginId;
    }

    // 회원가입 DTO
    @Data
    public static class DetailJoinRequestForm {
        @NotNull
        @NotBlank
        private String email;
        @NotNull
        @NotBlank
        private String loginId;
        @NotNull
        @NotBlank
        private String password;
        @NotNull
        @NotBlank
        private String department;
    }

    // 인증번호 DTO
    @Data
    public static class AuthEmailPayloadForm {
        @NotNull
        @NotBlank
        private long userId;
        @NotNull
        @NotBlank
        private String payload;
    }

    // 로그인 아이디 찾기 DTO
    @Data
    public static class FindLoginIdRequestForm {
        @NotNull
        @NotBlank
        private String email;
    }

    // 비밀번호 찾기 DTO
    @Data
    public static class FindPasswordRequestForm {
        @NotNull
        @NotBlank
        private String loginId;
        @NotNull
        @NotBlank
        private String email;
    }

    // 로그인 요청 DTO
    @Data
    public static class LoginRequestForm {
        @NotNull
        @NotBlank
        private String loginId;
        @NotNull
        @NotBlank
        private String password;
    }

    // 비밀번호 수정 DTO
    @Data
    public static class EditPasswordRequestForm {
        @NotNull
        @NotEmpty
        private long id;
        @NotNull
        @NotEmpty
        private String password;
    }

    // 회원탈퇴 요청 DTO
    @Data
    public static class QuitRequestForm {
        @NotNull
        @NotBlank
        private String loginId;
        @NotNull
        @NotBlank
        private String email;
        @NotNull
        @NotBlank
        private String password;
    }

    @Data
    public static class MannerEvaluationRequestForm {
        @NotNull
        @NotEmpty
        private long targetUserId;
        @NotNull
        @NotBlank
        private BigDecimal grade;
    }
}
