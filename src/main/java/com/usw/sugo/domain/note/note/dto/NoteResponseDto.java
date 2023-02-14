package com.usw.sugo.domain.note.note.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NoteResponseDto {

    @Getter
    @Builder
    public static class LoadNoteListForm {

        private Long noteId;
        private Long productPostId;
        private Long creatingUserId;
        private Long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private Integer requestUserUnreadCount;
        private LocalDateTime recentChattingDate;

        @QueryProjection
        public LoadNoteListForm(
            Long noteId, Long productPostId, Long creatingUserId, Long opponentUserId,
            String opponentUserNickname, String recentContent, Integer requestUserUnreadCount,
            LocalDateTime recentChattingDate) {
            this.noteId = noteId;
            this.productPostId = productPostId;
            this.creatingUserId = creatingUserId;
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

        private Long requestUserId;
        private Long productPostId;
        private Long noteContentId;
        private String message;
        private Long messageSenderId;
        private Long messageReceiverId;
        private LocalDateTime messageCreatedAt;
        private Long noteFileId;
        private String imageLink;
        private Long fileSenderId;
        private Long fileReceiverId;
        private LocalDateTime fileCreatedAt;

        @QueryProjection
        public LoadNoteAllContentForm(
            Long productPostId, Long noteContentId, String message,
            Long messageSenderId, Long messageReceiverId, LocalDateTime messageCreatedAt,
            Long noteFileId, String imageLink, Long fileSenderId, Long fileReceiverId,
            LocalDateTime fileCreatedAt) {
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

        public void setRequestUserId(Long requestUserId) {
            this.requestUserId = requestUserId;
        }
    }
}
