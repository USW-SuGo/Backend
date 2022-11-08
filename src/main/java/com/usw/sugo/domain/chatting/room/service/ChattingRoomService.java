package com.usw.sugo.domain.chatting.room.service;

import com.usw.sugo.domain.chatting.room.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ChattingRoomRepository roomRepository;

    /*
    최근 채팅이 1주일이 지난 채팅방 삭제
     */
    @Scheduled(cron = "0 * * * * *")
    public void autoDeleteChattingRoom() {
        roomRepository.deleteBeforeWeek();
    }
}
