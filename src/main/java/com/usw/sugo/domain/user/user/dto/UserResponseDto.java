package com.usw.sugo.domain.user.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {

    @Data
    public static class IsEmailExistResponse {
        boolean exist;
    }

    @Data
    public static class IsLoginIdExistResponse {
        boolean exist;
    }

    @Data
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class UserPageResponse {
        private long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private long countMannerEvaluation;
        private long countTradeAttempt;
        private List<MyPosting> myPosting;
        private List<LikePosting> likePosting;

        @QueryProjection
        public UserPageResponse(
                long userId, String email, String nickname, BigDecimal mannerGrade,
                long countMannerEvaluation, long countTradeAttempt) {
            this.userId = userId;
            this.email = email;
            this.nickname = nickname;
            this.mannerGrade = mannerGrade;
            this.countMannerEvaluation = countMannerEvaluation;
            this.countTradeAttempt = countTradeAttempt;
        }
    }

    @Data
    public static class MyPosting {
        private long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private int price;
        private String category;
        private boolean status;

        @QueryProjection
        public MyPosting(
                long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                int price, String category, boolean status) {
            this.productPostId = productPostId;
            this.imageLink = imageLink;
            this.contactPlace = contactPlace;
            this.updatedAt = updatedAt;
            this.title = title;
            this.price = price;
            this.category = category;
            this.status = status;
        }
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
        private boolean status;

        @QueryProjection
        public LikePosting(
                long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                int price, String category, boolean status) {
            this.productPostId = productPostId;
            this.imageLink = imageLink;
            this.contactPlace = contactPlace;
            this.updatedAt = updatedAt;
            this.title = title;
            this.price = price;
            this.category = category;
            this.status = status;
        }
    }
}
