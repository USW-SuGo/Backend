package com.usw.sugo.domain.productpost.productpost.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

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
        private Boolean status;

        @QueryProjection
        public SearchResultResponse(
                Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                Integer price, String nickname, String category, Boolean status) {
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
        private int price;
        private String nickname;
        private String category;
        private Boolean status;

        @QueryProjection
        public MainPageResponse(
                Long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
                int price, String nickname, String category, boolean status) {
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

    // 특정 게시물 조회에 대한 반환 DTO
    @Data
    public static class DetailPostResponse {
        private long productPostId;
        private long writerId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private String content;
        private int price;
        private String nickname;
        private String category;
        private Boolean status;
        private Boolean userLikeStatus;

        @QueryProjection
        public DetailPostResponse(
                long productPostId, long writerId, String imageLink, String contactPlace, LocalDateTime updatedAt,
                String title, String content, int price, String nickname, String category, boolean status) {
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
