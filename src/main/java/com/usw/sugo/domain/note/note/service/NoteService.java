package com.usw.sugo.domain.note.note.service;

import com.usw.sugo.domain.note.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    /*
    최근 쪽지가 한달이 지난 채팅방 삭제
    */
//    @Scheduled(cron = "0 * * * * *")
//    public void autoDeleteChattingRoom() {
//        noteRepository.deleteBeforeWeek();
//    }
}
