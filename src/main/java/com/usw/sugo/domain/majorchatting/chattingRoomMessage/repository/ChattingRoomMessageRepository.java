package com.usw.sugo.domain.majorchatting.chattingRoomMessage.repository;

import com.usw.sugo.domain.majorchatting.ChattingRoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomMessageRepository extends JpaRepository<ChattingRoomMessage, Long> {

}
