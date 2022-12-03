package com.usw.sugo.domain.note.note.service;

import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public void deleteNote(User requestUser) {
        noteRepository.deleteByCreatingUser(requestUser);
        noteRepository.deleteByOpponentUser(requestUser);
    }
}
