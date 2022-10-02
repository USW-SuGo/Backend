package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ChatRequest {

    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    /**
     * 송신자 id
     */
    @NotNull
    private Long senderId;

    /**
     * 수신자 id
     */
    @NotNull
    private Long receiverId;

    /**
     * 채팅방 id
     */
    @NotNull
    private Long roomId;

    /**
     * 메시지 내용
     */
    @NotBlank
    private String message;

    public ChatRequest(Long senderId, Long receiverId, Long roomId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.roomId = roomId;
        this.message = message;
    }
}
