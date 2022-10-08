package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import javax.validation.constraints.NotNull;

public class EnterRequest {

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
}
