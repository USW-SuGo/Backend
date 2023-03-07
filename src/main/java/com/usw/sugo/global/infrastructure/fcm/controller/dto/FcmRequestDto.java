package com.usw.sugo.global.infrastructure.fcm.controller.dto;

import lombok.Getter;

public class FcmRequestDto {

    @Getter
    public static class UpdateFcmTokenForm {

        private String fcmToken;
    }
}
