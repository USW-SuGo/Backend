package com.usw.sugo.domain.majorproduct.dto;

import lombok.Data;


public class PostRequestDto {

    @Data
    public static class PostingContentRequest {
        private String title;
        private String content;
        private int price;
        private String contactPlace;
        private String category;
    }

    @Data
    public static class UpPostingRequest {
        private long productPostId;
    }

}
