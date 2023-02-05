package com.usw.sugo.domain.user.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {

    @Getter
    @Builder
    public static class IsEmailExistResponseForm {
        Boolean exist;
    }

    @Getter
    @Builder
    public static class IsLoginIdExistResponseForm {
        Boolean exist;
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
    public static class UserPageResponseForm {
        private Long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private Long countMannerEvaluation;
        private Long countTradeAttempt;
        private List<MyPosting> myPostings;
        private List<LikePosting> likePostings;

        @QueryProjection
        public UserPageResponseForm(
                Long userId, String email, String nickname, BigDecimal mannerGrade,
                Long countMannerEvaluation, Long countTradeAttempt) {
            this.userId = userId;
            this.email = email;
            this.nickname = nickname;
            this.mannerGrade = mannerGrade;
            this.countMannerEvaluation = countMannerEvaluation;
            this.countTradeAttempt = countTradeAttempt;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class MyPosting {
        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Boolean status;

        @QueryProjection
        public MyPosting(
                Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                Integer price, String category, Boolean status) {
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
        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Boolean status;

        @QueryProjection
        public LikePosting(
                Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                Integer price, String category, Boolean status) {
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
    @Setter
    @Builder(access = AccessLevel.PROTECTED)
    public static class ClosePosting {
        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Boolean status;

        @QueryProjection
        public ClosePosting(
                Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                Integer price, String category, Boolean status) {
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
