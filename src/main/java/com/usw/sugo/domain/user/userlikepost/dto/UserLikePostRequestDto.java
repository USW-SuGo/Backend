package com.usw.sugo.domain.user.userlikepost.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

public class UserLikePostRequestDto {

    @Data
    public static class LikePostRequest {

        @NotNull
        private Long productPostId;
    }

    @Data
    public static class DeleteLikePost {
        @NotNull
        private Long productPostId;
    }
}
