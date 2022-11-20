package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class NoteResponseDto {

    @Data
    public static class LoadNoteListForm {
        private long noteId;
        private long requestUserId;
        private long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private int requestUserUnreadCount;
        private LocalDateTime recentChattingDate;
    }

    @Data
    public static class LoadNoteAllContentForm {
        private long requestUserId;
        private long noteContentId;
        private String message;
        private long messageSenderId;
        private long messageReceiverId;
        private LocalDateTime messageCreatedAt;
        private long noteFileId;
        private String imageLink;
        private long fileSenderId;
        private long fileReceiverId;
        private LocalDateTime fileCreatedAt;
    }
}
