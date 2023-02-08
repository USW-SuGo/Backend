package com.usw.sugo.domain.note.note.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

import static com.usw.sugo.global.exception.ExceptionType.NOTE_NOT_FOUNDED;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserServiceUtility userServiceUtility;
    private final ProductPostService productPostService;

    public Map<String, Long> executeCreatingRoom(Long creatingRequestUserId, Long opponentUserId, Long productPostId) {
        User validatedCreatingRequestUser = userServiceUtility.loadUserById(creatingRequestUserId);
        User validatedOpponentUser = userServiceUtility.loadUserById(opponentUserId);
        ProductPost validatedProductPost = productPostService.loadProductPostById(productPostId);
        validatedCreatingRequestUser.addCountTradeAttempt();
        validatedOpponentUser.addCountTradeAttempt();
        Note note = saveNote(validatedProductPost, validatedCreatingRequestUser, validatedOpponentUser);
        return new HashMap<>() {{
            put("noteId", note.getId());
        }};
    }

    public Stream<LoadNoteListForm> executeLoadAllNotes(User user, Pageable pageable) {
        User requestUser = userServiceUtility.loadUserById(user.getId());
        List<List<LoadNoteListForm>> noteListResult = noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);

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

    public Note loadNoteByNoteId(Long noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            return note.get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    public Note loadNoteBySenderAndNoteId(User user, Long noteId) {
        Optional<Note> creatingUserNote = noteRepository.findByCreatingUserAndId(user, noteId);
        Optional<Note> opponentUserNote = noteRepository.findByOpponentUserAndId(user, noteId);
        if (noteRepository.findByCreatingUserAndId(user, noteId).isPresent()) {
            return creatingUserNote.get();
        } else if (noteRepository.findByOpponentUserAndId(user, noteId).isPresent()) {
            return opponentUserNote.get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    private Note saveNote(ProductPost productPost, User creatingRequestUser, User opponentUser) {
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
        return note;
    }

    public void updateRecentContent(Note note, String message) {
        note.updateRecentContent(message);
    }

    public void updateUnreadCountByNoteAndReceiver(Note note, User user) {
        note.updateUserUnreadCount(user);
    }

    // 쪽지 방 읽음 처리
    public void readNoteRoom(Long noteId, User user) {
        Note note = loadNoteByNoteId(noteId);
        note.resetUserUnreadCount(user);
    }

    public void deleteNoteByUser(User user) {
        noteRepository.deleteByCreatingUser(user);
        noteRepository.deleteByOpponentUser(user);
    }

    public void deleteNoteByNoteId(Long noteId) {
        noteRepository.deleteById(noteId);
    }
}
