package com.usw.sugo.domain.note.note.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.usw.sugo.global.exception.ExceptionType.NOTE_NOT_FOUNDED;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

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

        creatingRequestUser.addCountTradeAttempt();
        opponentUser.addCountTradeAttempt();
        return note.getId();
    }

    public void deleteNoteByUser(User user) {
        noteRepository.deleteByCreatingUser(user);
        noteRepository.deleteByOpponentUser(user);
    }

    public void deleteNoteByNoteId(Long noteId) {
        noteRepository.deleteById(noteId);
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

    public Note loadNoteById(Long noteId) {
        if (noteRepository.findById(noteId).isPresent()) {
            return noteRepository.findById(noteId).get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }
}
