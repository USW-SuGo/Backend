package com.usw.sugo.domain.productpost.productpost.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class PostRequestDto {

    @Getter
    @Setter
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
    @Setter
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
