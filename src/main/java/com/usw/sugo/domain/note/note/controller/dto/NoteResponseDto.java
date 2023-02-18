package com.usw.sugo.domain.note.note.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
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
}
