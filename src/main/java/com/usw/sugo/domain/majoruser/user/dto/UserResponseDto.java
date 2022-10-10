package com.usw.sugo.domain.majoruser.user.dto;

import com.usw.sugo.domain.majoruser.UserLikePost;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {

    @Data
    public static class IsEmailExistResponse {
        boolean exist;

        public IsEmailExistResponse(boolean exist) {
            this.exist = exist;
        }
    }

    @Data
    public static class IsLoginIdExistResponse {
        boolean exist;

        public IsLoginIdExistResponse(boolean exist) {
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
    public static class MyPageResponse {
        private long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private long countMannerEvaluation;
        private long countTradeAttempt;
        private List<MyPosting> myPosting;
        private List<LikePosting> likePosting;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherUserPageResponse {
        private long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private long countMannerEvaluation;
        private long countTradeAttempt;
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

    @Data
    public static class LikePosting {
        private long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private int price;
        private String category;
    }
}
