package com.usw.sugo.domain.user.user.controller.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequestForm {

        @NotBlank
        private String email;
    }

    @Data
    public static class IsLoginIdExistRequestForm {

        @NotBlank
        private String loginId;
    }

    @Data
    public static class DetailJoinRequestForm {

        @NotBlank
        private String email;
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
        @NotBlank
        private String department;
    }

    // 인증번호 DTO
    @Data
    public static class AuthEmailPayloadForm {

        @NotNull
        private Long userId;
        @NotBlank
        private String payload;
    }

    @Data
    public static class FindLoginIdRequestForm {

        @NotBlank
        private String email;
    }

    @Data
    public static class FindPasswordRequestForm {

        @NotBlank
        private String loginId;
        @NotBlank
        private String email;
    }

    @Data
    public static class LoginRequestForm {

        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
    }

    @Data
    public static class EditPasswordRequestForm {

        @NotBlank
        private String prePassword;

        @NotBlank
        private String newPassword;
    }

    @Data
    public static class QuitRequestForm {

        @NotBlank
        private String loginId;
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class MannerEvaluationRequestForm {

        @NotNull
        private Long targetUserId;
        @NotNull
        private BigDecimal grade;
    }

    @Getter
    public static class PushAlarmStatusRequestForm {

        @NotNull
        private Boolean pushAlarmStatus;
    }

    @Getter
    public static class RegisterFcmTokenRequestForm {

        @NotBlank
        private String fcmToken;
    }
}
