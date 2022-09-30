package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.usw.sugo.domain.majorchatting.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long>, CustomChattingRoomRepository {
}
