package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import lombok.Data;

@Data
public class ChattingRoomRequest {

    @Data
    public static class CreateChattingRoomRequest {
        String nickname;
    }
}
