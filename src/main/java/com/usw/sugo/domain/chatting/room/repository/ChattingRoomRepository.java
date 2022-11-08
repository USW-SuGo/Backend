package com.usw.sugo.domain.chatting.room.repository;

import com.usw.sugo.domain.chatting.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long>, CustomChattingRoomRepository {
}
