package com.usw.sugo.domain.notice.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class NoticeRequestDto {

    @Getter
    public static class NoticePostRequest {
        @NotEmpty
        @NotBlank
        private String title;
        @NotEmpty
        @NotBlank
        private String content;
    }

    @Getter
    public static class NoticeUpdateRequest {
        @NotEmpty
        @NotBlank
        private Long noticeId;
        @NotEmpty
        @NotBlank
        private String title;
        @NotEmpty
        @NotBlank
        private String content;
    }

    @Getter
    public static class NoticeDeleteRequest {
        @NotEmpty
        @NotBlank
        private Long noticeId;
    }

}
