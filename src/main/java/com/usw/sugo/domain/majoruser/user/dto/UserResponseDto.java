package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class UserResponseDto {

    @Data
    @RequiredArgsConstructor
    public static class IsEmailExistResponse {
        boolean exist;

         public IsEmailExistResponse(boolean exist){
             this.exist = exist;
         }
    }

}
