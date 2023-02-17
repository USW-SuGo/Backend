package com.usw.sugo.domain.note.notefile.controller.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NoteFileRequestDto {

    @Getter
    @AllArgsConstructor
    public static class SendNoteFileForm {

        @NotNull
        private Long noteId;

        @NotNull
        private Long receiverId;
    }
}