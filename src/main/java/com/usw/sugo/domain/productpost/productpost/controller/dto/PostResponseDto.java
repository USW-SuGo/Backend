package com.usw.sugo.domain.productpost.productpost.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostResponseDto {

    @Data
    public static class SearchResultResponse {

        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String nickname;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;

        @QueryProjection
        public SearchResultResponse(
            Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt,
            String title, Integer price, String nickname, String category, Boolean status) {
            this.productPostId = productPostId;
            this.imageLink = imageLink;
            this.contactPlace = contactPlace;
            this.updatedAt = updatedAt;
            this.title = title;
            this.price = price;
            this.nickname = nickname;
            this.category = category;
            this.status = status;
        }
    }

    @Data
    public static class MainPageResponse {

        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String nickname;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;

        @QueryProjection
        public MainPageResponse(
            Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt,
            String title, Integer price, String nickname, String category, Boolean status) {
            this.productPostId = productPostId;
            this.imageLink = imageLink;
            this.contactPlace = contactPlace;
            this.updatedAt = updatedAt;
            this.title = title;
            this.price = price;
            this.nickname = nickname;
            this.category = category;
            this.status = status;
        }
    }

    @Data
    public static class DetailPostResponse {

        private Long productPostId;
        private Long writerId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private String content;
        private Integer price;
        private String nickname;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;
        private Boolean userLikeStatus;

        @QueryProjection
        public DetailPostResponse(
            Long productPostId, Long writerId, String imageLink, String contactPlace,
            LocalDateTime updatedAt, String title, String content, Integer price, String nickname,
            String category, Boolean status) {
            this.productPostId = productPostId;
            this.writerId = writerId;
            this.imageLink = imageLink;
            this.contactPlace = contactPlace;
            this.updatedAt = updatedAt;
            this.title = title;
            this.content = content;
            this.price = price;
            this.nickname = nickname;
            this.category = category;
            this.status = status;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPosting {

        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;

        @QueryProjection
        public MyPosting(
            Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt,
            String title, Integer price, String category, Boolean status) {
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikePosting {

        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;

        @QueryProjection
        public LikePosting(
            Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt,
            String title, Integer price, String category, Boolean status) {
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClosePosting {

        private Long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private Integer price;
        private String category;
        private Integer likeCount;
        private Integer noteCount;
        private Boolean status;

        @QueryProjection
        public ClosePosting(
            Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt,
            String title, Integer price, String category, Boolean status) {
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
