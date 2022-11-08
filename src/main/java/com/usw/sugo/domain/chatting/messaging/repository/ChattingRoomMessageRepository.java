package com.usw.sugo.domain.chatting.messaging.repository;

import com.usw.sugo.domain.chatting.ChattingRoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomMessageRepository extends JpaRepository<ChattingRoomMessage, Long> {

}
