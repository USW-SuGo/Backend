package com.usw.sugo.domain.note.note.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NoteResponseDto {

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoadNoteListForm {

        private String imageLink;
        private Long noteId;
        private Long productPostId;
        private Long creatingUserId;
        private Long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private Integer requestUserUnreadCount;
        private LocalDateTime recentChattingDate;

        @QueryProjection
        public LoadNoteListForm(Long noteId, Long productPostId,
            Long creatingUserId, Long opponentUserId, String opponentUserNickname,
            String recentContent, Integer requestUserUnreadCount,
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
    @Setter
    @Builder
    @AllArgsConstructor
    public static class LoadNoteAllContentForm {

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
            Long messageSenderId, Long messageReceiverId, LocalDateTime messageCreatedAt) {
            this.productPostId = productPostId;
            this.noteContentId = noteContentId;
            this.message = message;
            this.messageSenderId = messageSenderId;
            this.messageReceiverId = messageReceiverId;
            this.messageCreatedAt = messageCreatedAt;
        }

        @QueryProjection
        public LoadNoteAllContentForm(
            Long noteFileId, String imageLink, Long fileSenderId, Long fileReceiverId,
            LocalDateTime fileCreatedAt) {
            this.noteFileId = noteFileId;
            this.imageLink = imageLink;
            this.fileSenderId = fileSenderId;
            this.fileReceiverId = fileReceiverId;
            this.fileCreatedAt = fileCreatedAt;
        }

        static Comparator<LoadNoteAllContentForm> customComparator = new Comparator<LoadNoteAllContentForm>() {
            @Override
            public int compare(LoadNoteAllContentForm o1, LoadNoteAllContentForm o2) {
                LocalDateTime o1Time = o1.getMessageCreatedAt() != null ? o1.getMessageCreatedAt() : o1.getFileCreatedAt();
                LocalDateTime o2Time = o2.getMessageCreatedAt() != null ? o2.getMessageCreatedAt() : o2.getFileCreatedAt();
                return o1Time.compareTo(o2Time);
            }
        };
    }
}
