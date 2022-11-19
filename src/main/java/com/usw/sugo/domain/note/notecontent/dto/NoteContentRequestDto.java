package com.usw.sugo.domain.note.notecontent.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

public class NoteContentRequestDto {

    @Data
    public static class SendNoteContentForm {
        @NotNull
        private long noteId;

        @NotNull
        private String message;

        @NotNull
        private long senderId;

        @NotNull
        private long receiverId;
    }

}
