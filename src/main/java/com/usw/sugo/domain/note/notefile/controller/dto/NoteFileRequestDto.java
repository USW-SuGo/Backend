package com.usw.sugo.domain.note.notefile.controller.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

public class NoteFileRequestDto {

    @Data
    public static class SendNoteFileForm {

        @NotNull
        private Long noteId;

        @NotNull
        private Long senderId;

        @NotNull
        private Long receiverId;
    }
}