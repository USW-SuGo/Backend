package com.usw.sugo.domain.chatting.room.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class ChattingRoomResponseDto {

    @Data
    public static class LoadChattingListForm {
        private long roomId;
        private long sellerId;
        private long buyerId;
        private String sellerNickname;
        private String buyerNickname;
        private String recentContent;
        private LocalDateTime recentChattingDate;
    }

    @Data
    public static class LoadChattingRoomForm {
        private long roomId;
        private long sellerId;
        private long buyerId;
        private String sellerNickname;
        private String buyerNickname;
        private String title;
        private String contactPlace;
        private int price;
    }

    @Data
    public static class LoadChattingRoomMessageForm {
        private long senderId;
        private long receiverId;
        private String message;
        private LocalDateTime createdAt;
    }

    @Data
    public static class LoadChattingRoomFileForm {
        private long senderId;
        private long receiverId;
        private String imageLink;
        private LocalDateTime createdAt;
    }
}
