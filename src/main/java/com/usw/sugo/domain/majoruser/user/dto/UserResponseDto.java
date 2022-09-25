package com.usw.sugo.domain.majoruser.user.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
        private String accessToken;
        private String refreshToken;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPageResponse {
        private long userId;
        private String email;
        private String nickname;
        private List<MyPosting> myPosting;
    }

    @Data
    public static class MyPosting {
        private long id;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private int price;
        private String category;
    }
}
