package com.usw.sugo.domain.note.service;

import com.usw.sugo.domain.note.Note;
import com.usw.sugo.domain.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public long makeNote(ProductPost productPost, User creatingRequestUser, User opponentUser) {
        Note note = Note.builder()
                .productPost(productPost)
                .creatingUser(creatingRequestUser)
                .creatingUserNickname(creatingRequestUser.getNickname())
                .creatingUserUnreadCount(0)
                .opponentUser(opponentUser)
                .opponentUserNickname(opponentUser.getNickname())
                .opponentUserUnreadCount(0)
                .build();

        noteRepository.save(note);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(creatingRequestUser.getId(), opponentUser.getId());

        return note.getId();
    }

    public Stream<LoadNoteListForm> loadNoteList(User requestUser, Pageable pageable) {
        List<List<LoadNoteListForm>> noteListResult =
                noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);

        List<LoadNoteListForm> loadNoteListFormRequestUserIsCreatingNote = noteListResult.get(0);
        List<LoadNoteListForm> loadNoteListFormsRequestUserIsCreatedNote = noteListResult.get(1);

        List<LoadNoteListForm> tempResult = new ArrayList<>();
        tempResult.addAll(loadNoteListFormRequestUserIsCreatingNote);
        tempResult.addAll(loadNoteListFormsRequestUserIsCreatedNote);

        return tempResult
                .stream()
                .sorted(Comparator.comparing(LoadNoteListForm::getRecentChattingDate)
                        .reversed());
    }
}
