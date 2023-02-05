package com.usw.sugo.domain.productpost.productpost.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


public class PostRequestDto {

    @Getter
    public static class PostingRequest {
        @NotEmpty
        @NotBlank
        private String title;

        @NotEmpty
        @NotBlank
        private String content;

        @NotEmpty
        @NotBlank
        private Integer price;

        @NotEmpty
        @NotBlank
        private String contactPlace;

        @NotEmpty
        @NotBlank
        private String category;
    }

    @Getter
    public static class PutContentRequest {
        @NotEmpty
        @NotBlank
        private Long productPostId;

        @NotEmpty
        @NotBlank
        private String title;

        @NotEmpty
        @NotBlank
        private String content;

        @NotEmpty
        @NotBlank
        private int price;

        @NotEmpty
        @NotBlank
        private String contactPlace;

        @NotEmpty
        @NotBlank
        private String category;
    }

    @Getter
    public static class DeleteContentRequest {
        @NotEmpty
        @NotBlank
        private Long productPostId;
    }

    @Getter
    public static class UpPostingRequest {
        @NotEmpty
        @NotBlank
        private Long productPostId;
    }

    @Getter
    public static class ClosePostRequest {
        @NotEmpty
        @NotBlank
        private Long productPostId;
    }
}
