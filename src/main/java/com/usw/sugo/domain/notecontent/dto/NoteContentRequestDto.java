package com.usw.sugo.domain.notecontent.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteContentRequestDto {

    @Getter
    @NoArgsConstructor
    public static class SendNoteContentForm {
        @NotBlank
        private long noteId;

        @NotNull
        private String message;

        @NotBlank
        private long senderId;

        @NotBlank
        private long receiverId;
    }
}