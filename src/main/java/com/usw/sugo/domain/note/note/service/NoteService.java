package com.usw.sugo.domain.note.note.service;

import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_CREATE_YOURSELF;
import static com.usw.sugo.global.exception.ExceptionType.NOTE_NOT_FOUNDED;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.controller.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.domain.productpost.productpostfile.service.ProductPostFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.apiresult.ApiResultFactory;
import com.usw.sugo.global.exception.CustomException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserServiceUtility userServiceUtility;
    private final ProductPostService productPostService;
    private final NoteContentService noteContentService;
    private final ProductPostFileService productPostFileService;

    @Transactional
    public Map<String, Long> executeCreatingRoom(
        Long creatingRequestUserId, Long opponentUserId, Long productPostId) {

        if (validateNoteCreateRequest(creatingRequestUserId, opponentUserId, productPostId)) {
            Long alreadyNoteId = noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
                creatingRequestUserId, opponentUserId, productPostId).get().getId();
            return new HashMap<>() {{
                put("noteId", alreadyNoteId);
            }};
        }

        User validatedCreatingRequestUser = userServiceUtility.loadUserById(creatingRequestUserId);
        User validatedOpponentUser = userServiceUtility.loadUserById(opponentUserId);
        ProductPost validatedProductPost = productPostService.loadProductPostById(productPostId);
        validatedCreatingRequestUser.addCountTradeAttempt();
        validatedOpponentUser.addCountTradeAttempt();

        Note note = saveNote(
            validatedProductPost, validatedCreatingRequestUser, validatedOpponentUser);
        return new HashMap<>() {{
            put("noteId", note.getId());
        }};
    }

    @Transactional
    public List<Object> executeLoadAllNotes(User user, Pageable pageable) {
        User requestUser = userServiceUtility.loadUserById(user.getId());
        List<List<LoadNoteListForm>> notes =
            noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);

        setThumbnailImageLink(notes);

        List<LoadNoteListForm> loadedNotes = new ArrayList<>();
        loadedNotes.addAll(notes.get(0));
        loadedNotes.addAll(notes.get(1));

        List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", user.getId());
        }});
        result.add(sortLoadNoteListForm(loadedNotes));

        return result;
    }

    @Transactional
    public Map<String, Boolean> executeDeleteNote(User user, Long noteId) {
        Note note = loadNoteByNoteId(noteId);
        if (notRemainedUserInNote(note, user.getId())) {
            noteContentService.deleteByNote(note);
            return ApiResultFactory.getSuccessFlag();
        }
        if (note.getCreatingUserStatus().equals(user.getId())) {
            note.updateCreatingUserStatus();
            return ApiResultFactory.getSuccessFlag();
        }
        note.updateOpponentUserStatus();
        return ApiResultFactory.getSuccessFlag();
    }

    private boolean notRemainedUserInNote(Note note, Long userId) {
        if (note.getOpponentUser().getId().equals(userId)
            && note.getCreatingUserStatus() == false) {
            return true;
        } else if (note.getCreatingUser().getId().equals(userId)
            && note.getOpponentUserStatus() == false) {
            return true;
        }
        return false;
    }

    private List<List<LoadNoteListForm>> setThumbnailImageLink(List<List<LoadNoteListForm>> notes) {
        for (List<LoadNoteListForm> note : notes) {
            for (LoadNoteListForm loadNoteListForm : note) {
                ProductPost productPost =
                    productPostService.loadProductPostById(loadNoteListForm.getProductPostId());
                ProductPostFile productPostFile =
                    productPostFileService.loadProductPostFileByProductPost(productPost);
                String[] split = productPostFile.getImageLink().split(",");

                loadNoteListForm.setImageLink(
                    split[0].replace("[", "").replace("]", ""));
            }
        }
        return notes;
    }

    private Stream<LoadNoteListForm> sortLoadNoteListForm(List<LoadNoteListForm> loadedNotes) {
        return loadedNotes
            .stream()
            .sorted(Comparator.comparing(LoadNoteListForm::getRecentChattingDate).reversed());
    }

    public Note loadNoteByNoteId(Long noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            return note.get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    public List<Note> loadNotesByUserId(Long userId) {
        return noteRepository.findByUser(userServiceUtility.loadUserById(userId));
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

    @Transactional
    protected Note saveNote(ProductPost productPost, User creatingRequestUser, User opponentUser) {
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

    @Transactional
    public void updateRecentContent(Note note, String message) {
        note.updateRecentContent(message);
    }

    @Transactional
    public void updateUserUnreadCountBySendMessage(Note note, User user) {
        note.updateUserUnreadCountBySendMessage(user);
    }

    @Transactional
    public void updateUserUnreadCountByEnteredNote(Note note, User user) {
        note.updateUserUnreadCountByEnteredNote(user);
    }

    @Transactional
    public void deleteNotesByUser(User user) {
        noteRepository.deleteByCreatingUser(user);
        noteRepository.deleteByOpponentUser(user);
    }

    @Transactional
    protected void deleteNoteByNoteId(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    private boolean validateNoteCreateRequest(
        Long creatingUserId, Long opponentUserId, Long productPostId) {
        if (creatingUserId.equals(opponentUserId)) {
            throw new CustomException(DO_NOT_CREATE_YOURSELF);
        }

        Optional<Note> findNote = noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
            creatingUserId, opponentUserId, productPostId);
        return findNote.isPresent();
    }
}
