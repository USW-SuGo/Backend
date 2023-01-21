package com.usw.sugo.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {

    @Getter
    @Builder
    public static class IsEmailExistResponseForm {
        boolean exist;
    }

    @Getter
    @Builder
    public static class IsLoginIdExistResponseForm {
        boolean exist;
    }

    @Getter
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class UserPageResponseForm {
        private long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private long countMannerEvaluation;
        private long countTradeAttempt;
        private List<MyPosting> myPosting;
        private List<LikePosting> likePosting;

        @QueryProjection
        public UserPageResponseForm(
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

    @Getter
    @Builder
    @Setter
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

    @Getter
    @Builder(access = AccessLevel.PROTECTED)
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
