package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class UserResponseDto {

    @Data
    public static class IsEmailExistResponse {
        boolean exist;
         public IsEmailExistResponse(boolean exist){
             this.exist = exist;
         }
    }

    @Data
    public static class LoginResponse {
        String accessToken;
        String refreshToken;

    }

}
