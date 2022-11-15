package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteRequestDto {

    @Data
    public static class CreateNoteRequest {
        @NotNull
        @NotBlank
        private long creatingUserId;

        @NotNull
        @NotBlank
        private long opponentUserId;

        @NotNull
        @NotBlank
        private long productPostId;
    }

    @Data
    public static class LoadNoteRequest {
        @NotNull
        @NotBlank
        private long requestUser;
    }
}
