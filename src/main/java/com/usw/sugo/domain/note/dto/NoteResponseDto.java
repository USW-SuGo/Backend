package com.usw.sugo.domain.note.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NoteResponseDto {

    @Getter
    @Builder
    public static class LoadNoteListForm {
        private long noteId;
        private long productPostId;
        private long requestUserId;
        private long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private int requestUserUnreadCount;
        private LocalDateTime recentChattingDate;

        @QueryProjection
        public LoadNoteListForm(
                long noteId, long productPostId, long requestUserId, long opponentUserId,
                String opponentUserNickname, String recentContent, int requestUserUnreadCount,
                LocalDateTime recentChattingDate) {
            this.noteId = noteId;
            this.productPostId = productPostId;
            this.requestUserId = requestUserId;
            this.opponentUserId = opponentUserId;
            this.opponentUserNickname = opponentUserNickname;
            this.recentContent = recentContent;
            this.requestUserUnreadCount = requestUserUnreadCount;
            this.recentChattingDate = recentChattingDate;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoadNoteAllContentForm {
        private long requestUserId;
        private long productPostId;
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

        @QueryProjection
        public LoadNoteAllContentForm(
                long productPostId, long noteContentId, String message,
                long messageSenderId, long messageReceiverId, LocalDateTime messageCreatedAt,
                long noteFileId, String imageLink, long fileSenderId, long fileReceiverId, LocalDateTime fileCreatedAt) {
            this.productPostId = productPostId;
            this.noteContentId = noteContentId;
            this.message = message;
            this.messageSenderId = messageSenderId;
            this.messageReceiverId = messageReceiverId;
            this.messageCreatedAt = messageCreatedAt;
            this.noteFileId = noteFileId;
            this.imageLink = imageLink;
            this.fileSenderId = fileSenderId;
            this.fileReceiverId = fileReceiverId;
            this.fileCreatedAt = fileCreatedAt;
        }

        public void setRequestUserId(long requestUserId) {
            this.requestUserId = requestUserId;
        }
    }
}
