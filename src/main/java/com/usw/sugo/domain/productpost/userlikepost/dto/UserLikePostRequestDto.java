package com.usw.sugo.domain.productpost.userlikepost.dto;

import lombok.Data;

public class UserLikePostRequestDto {

    @Data
    public static class LikePostRequest {
        private long productPostId;
    }

    @Data
    public static class DeleteLikePost {
        private long productPostId;
    }
}
