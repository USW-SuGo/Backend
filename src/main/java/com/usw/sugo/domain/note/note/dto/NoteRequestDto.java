package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

public class NoteRequestDto {

    @Data
    public static class CreateNoteRequestForm {
        @NotNull
        private Long opponentUserId;

        @NotNull
        private Long productPostId;
    }
}
