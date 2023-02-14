package com.usw.sugo.domain.note.note.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

public class NoteRequestDto {

    @Data
    public static class CreateNoteRequestForm {

        @NotNull
        private Long opponentUserId;

        @NotNull
        private Long productPostId;
    }
}
