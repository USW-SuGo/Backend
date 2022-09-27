package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.usw.sugo.domain.majorchatting.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long>, CustomChattingRoomRepository {
    Optional<ChattingRoom> findByRoomId(String roomId);
}
