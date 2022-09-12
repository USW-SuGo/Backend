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

}
