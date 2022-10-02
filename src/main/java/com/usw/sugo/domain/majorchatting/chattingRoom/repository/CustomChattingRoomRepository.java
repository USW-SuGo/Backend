package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majoruser.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChattingRoomRepository {

    List<ChattingRoom> findAllRoomByRequestUserId(User user);

    void deleteBeforeWeek();

    void testDeleteBeforeWeek();
}
