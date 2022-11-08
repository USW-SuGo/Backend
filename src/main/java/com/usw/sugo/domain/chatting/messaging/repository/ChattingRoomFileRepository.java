package com.usw.sugo.domain.chatting.messaging.repository;

import com.usw.sugo.domain.chatting.entity.ChattingRoomFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRoomFileRepository extends JpaRepository<ChattingRoomFile, Long> {

}
