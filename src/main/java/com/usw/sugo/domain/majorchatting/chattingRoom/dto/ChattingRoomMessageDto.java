package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import lombok.Data;

public class ChattingRoomMessageDto {

    @Data
    public static class ChatMessage {
        public enum MessageType{
            ENTER, TALK
        }

        private MessageType type;
        private String roomId;
        private String sender;
        private String message;
    }
}
