package com.usw.sugo.domain.majornote.dto;

import lombok.Data;

public class NoteRequestDto {

    @Data
    public static class CreateNoteRequest {
        private long sellerId;
        private long buyerId;
        private long productPostId;
    }

    @Data
    public static class LoadNoteRequest {
        private long requestUser;
    }
}
