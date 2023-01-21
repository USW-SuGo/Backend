package com.usw.sugo.domain.productpost.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

public class PostResponseDto {

    @Data
    public static class SearchResultResponse {
        private long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private int price;
        private String nickname;
        private String category;
        private boolean status;

        @QueryProjection
        public SearchResultResponse(
                long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
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

    // 전체 포스트 조회에 대한 DTO
    @Data
    public static class MainPageResponse {
        private long productPostId;
        private String imageLink;
        private String contactPlace;
        private LocalDateTime updatedAt;
        private String title;
        private int price;
        private String nickname;
        private String category;
        private boolean status;

        @QueryProjection
        public MainPageResponse(
                long productPostId, String imageLink, String contactPlace, LocalDateTime updatedAt, String title,
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
        private boolean status;
        private boolean userLikeStatus;

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
}
