package com.usw.sugo.domain.note.notecontent.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteContentRequestDto {

    @Data
    public static class SendNoteContentForm {
        @NotNull
        @NotBlank
        private long noteId;

        @NotNull
        @NotBlank
        private String message;

        @NotNull
        @NotBlank
        private long senderId;

        @NotNull
        @NotBlank
        private long receiverId;
    }

}
