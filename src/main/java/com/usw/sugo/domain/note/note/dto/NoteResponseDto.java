package com.usw.sugo.domain.note.note.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class NoteResponseDto {

    @Data
    public static class LoadNoteListCreatingByRequestUserForm {
        private long roomId;
        private long opponentUserId;
        private String opponentUserNickname;
        private String recentContent;
        private int creatingUserUnreadCount;
        private LocalDateTime recentChattingDate;
    }

    @Data
    public static class LoadNoteListCreatingByOpponentUserForm {
        private long id;
        private long creatingUserId;
        private String creatingUserNickname;
        private String recentContent;
        private int opponentUserUnreadCount;
        private LocalDateTime recentChattingDate;
    }

    @Data
    public static class LoadNoteForm {
        private long id;
        private long sellerId;
        private long buyerId;
        private String sellerNickname;
        private String buyerNickname;
        private String title;
        private String contactPlace;
        private int price;
    }

    @Data
    public static class LoadNoteMessageForm {
        private long senderId;
        private long receiverId;
        private String message;
        private LocalDateTime createdAt;
    }

    @Data
    public static class LoadNoteFileForm {
        private long senderId;
        private long receiverId;
        private String imageLink;
        private LocalDateTime createdAt;
    }
}
