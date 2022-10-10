package com.usw.sugo.domain.notice.dto;

import lombok.Data;

public class NoticeRequestDto {

    @Data
    public static class NoticePostRequest {
        private String title;
        private String content;
    }

    @Data
    public static class NoticeUpdateRequest {
        private long noticeId;
        private String title;
        private String content;
    }

    @Data
    public static class NoticeDeleteRequest {
        private long id;
    }

}
