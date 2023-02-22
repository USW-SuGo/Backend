package com.usw.sugo.global.fcm.controller.dto;

import lombok.Getter;

public class FcmRequestDto {

    @Getter
    public static class UpdateFcmTokenForm {

        private String fcmToken;
    }
}
