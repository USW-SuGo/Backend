package com.usw.sugo.domain.note.service;

import com.usw.sugo.domain.note.Note;
import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public void deleteNote(User requestUser) {
        noteRepository.deleteByCreatingUser(requestUser);
        noteRepository.deleteByOpponentUser(requestUser);
    }

    public long makeNote(ProductPost productPost, User creatingRequestUser, User opponentUser) {
        Note note = Note.builder()
                .productPost(productPost)
                .creatingUser(creatingRequestUser)
                .creatingUserNickname(creatingRequestUser.getNickname())
                .creatingUserUnreadCount(0)
                .opponentUser(opponentUser)
                .opponentUserNickname(opponentUser.getNickname())
                .opponentUserUnreadCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(creatingRequestUser.getId(), opponentUser.getId());

        return note.getId();
    }
}
