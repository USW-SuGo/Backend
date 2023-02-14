package com.usw.sugo.domain.note.notecontent.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NoteContentRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SendNoteContentForm {

        @NotNull
        private Long noteId;

        @NotEmpty
        private String message;

        @NotNull
        private Long senderId;

        @NotNull
        private Long receiverId;
    }
}