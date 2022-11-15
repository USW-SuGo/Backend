package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

public class NoteRequestDto {

    @Data
    public static class CreateNoteRequest {
        private long creatingUserId;
        private long opponentUserId;
        private long productPostId;
    }

    @Data
    public static class LoadNoteRequest {
        private long requestUser;
    }
}
