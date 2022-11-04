package com.usw.sugo.domain.majorproduct.dto;

import lombok.Data;


public class PostRequestDto {

    @Data
    public static class PostingRequest {
        private String title;
        private String content;
        private int price;
        private String contactPlace;
        private String category;
    }

    @Data
    public static class PutContentRequest {
        private long productPostId;
        private String title;
        private String content;
        private int price;
        private String contactPlace;
        private String category;
    }

    @Data
    public static class DeleteContentRequest {
        private long productPostId;
    }

    @Data
    public static class UpPostingRequest {
        private long productPostId;
    }

    @Data
    public static class ClosePostRequest {
        private long productPostId;
    }
}
