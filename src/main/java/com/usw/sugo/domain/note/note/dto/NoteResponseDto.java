package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class NoteResponseDto {

    @Data
    public static class LoadNoteListForm {
        private long roomId;
        private long requestUserId;
        private long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private int requestUserUnreadCount;
        private LocalDateTime recentChattingDate;
    }

    @Data
    public static class LoadNoteRoomForm {
        private String message;
        private long messageSenderId;
        private long messageReceiverId;
        private LocalDateTime messageCreatedAt;
        private String imageLink;
        private long fileSenderId;
        private long fileReceiverId;
        private LocalDateTime fileCreatedAt;
    }

    @Data
    public static class LoadNoteMessageForm {
        private long senderId;
        private long receiverId;
        private String message;
        private LocalDateTime createdAt;
    }

    @Data
    public static class LoadNoteFileForm {
        private long senderId;
        private long receiverId;
        private String imageLink;
        private LocalDateTime createdAt;
    }
}
