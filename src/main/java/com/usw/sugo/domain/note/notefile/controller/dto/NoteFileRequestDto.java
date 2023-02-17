package com.usw.sugo.domain.note.notefile.controller.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoteFileRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendNoteFileForm {

        @NotNull
        private Long noteId;

        @NotNull
        private Long receiverId;
    }
}