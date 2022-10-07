package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import lombok.Data;

public class ChattingRoomRequestDto {

    @Data
    public static class CreateRoomRequest {
        private long sellerId;
        private long buyerId;
        private long productPostId;
    }

    @Data
    public static class TestCreateRoom {
        String name;
    }


    @Data
    public static class LoadRoomRequest {
        private long requestUser;
    }
}
