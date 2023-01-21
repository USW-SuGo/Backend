package com.usw.sugo.domain.notefile.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteFileRequestDto {

    @Data
    public static class SendNoteFileForm {
        @NotNull
        @NotBlank
        private long noteId;

        @NotNull
        @NotBlank
        private long senderId;

        @NotNull
        @NotBlank
        private long receiverId;
    }

}