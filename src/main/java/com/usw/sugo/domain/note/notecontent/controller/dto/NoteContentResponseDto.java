package com.usw.sugo.domain.note.notecontent.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoteContentResponseDto {


    @Getter
    @Builder
    @NoArgsConstructor
    public static class LoadNoteAllContentForm {

        private Long productPostId;
        private Long noteContentId;
        private String message;
        private String imageLink;
        private Long senderId;
        private Long receiverId;
        private LocalDateTime createdAt;

        @QueryProjection
        public LoadNoteAllContentForm(
            Long productPostId, Long noteContentId, String message, String imageLink,
            Long senderId, Long receiverId, LocalDateTime createdAt) {
            this.productPostId = productPostId;
            this.noteContentId = noteContentId;
            this.message = message;
            this.imageLink = imageLink;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.createdAt = createdAt;
        }

        public void setImageLink(String imageLink) {
            this.imageLink = imageLink;
        }
    }
}
