package com.usw.sugo.domain.productpost.productpost.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class PostRequestDto {

    @Getter
    @AllArgsConstructor
    public static class PostingRequest {

        @NotBlank
        private String title;

        @NotBlank
        private String content;

        @NotNull
        private Integer price;

        @NotBlank
        private String contactPlace;

        @NotBlank
        private String category;
    }

    @Getter
    @AllArgsConstructor
    public static class PutContentRequest {

        @NotNull
        private Long productPostId;

        @NotBlank
        private String title;

        @NotBlank
        private String content;

        @NotNull
        private Integer price;

        @NotBlank
        private String contactPlace;

        @NotBlank
        private String category;
    }

    @Getter
    public static class DeleteContentRequest {

        @NotNull
        private Long productPostId;
    }

    @Getter
    public static class UpPostingRequest {

        @NotNull
        private Long productPostId;
    }

    @Getter
    public static class ClosePostRequest {

        @NotNull
        private Long productPostId;
    }
}
