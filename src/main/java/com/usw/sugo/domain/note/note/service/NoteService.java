package com.usw.sugo.domain.note.note.service;

import static com.usw.sugo.global.apiresult.ApiResultFactory.getSuccessFlag;
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
        Long creatingRequestUserId, Long opponentUserId, Long productPostId
    ) {

        if (validateNoteCreateRequest(creatingRequestUserId, opponentUserId, productPostId)) {
            Long alreadyNoteId = noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
                creatingRequestUserId, opponentUserId, productPostId).get().getId();
            return new HashMap<>() {{
                put("noteId", alreadyNoteId);
            }};
        }

        final User validatedCreatingRequestUser = userServiceUtility.loadUserById(
            creatingRequestUserId);
        final User validatedOpponentUser = userServiceUtility.loadUserById(opponentUserId);
        final ProductPost validatedProductPost = productPostService.loadProductPostById(
            productPostId);
        validatedCreatingRequestUser.addCountTradeAttempt();
        validatedOpponentUser.addCountTradeAttempt();

        final Note note = saveNote(
            validatedProductPost, validatedCreatingRequestUser, validatedOpponentUser);
        return new HashMap<>() {{
            put("noteId", note.getId());
        }};
    }

    @Transactional
    public List<Object> executeLoadAllNotes(User user, Pageable pageable) {
        final User requestUser = userServiceUtility.loadUserById(user.getId());
        final List<List<LoadNoteListForm>> notes =
            noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);
        setThumbnailImageLink(notes);

        final List<LoadNoteListForm> loadedNotes = new ArrayList<>(notes.get(0));

        final List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", user.getId());
        }});
        result.add(sortLoadNoteListForm(loadedNotes));

        return result;
    }

    @Transactional
    public Map<String, Boolean> executeDeleteNote(User user, Long noteId) {
        final Note note = loadNoteByNoteId(noteId);
        if (notRemainedUserInNote(note, user.getId())) {
            noteContentService.deleteByNote(note);
            noteRepository.deleteById(note.getId());
            return getSuccessFlag();
        } else if (note.getCreatingUser().getId().equals(user.getId())) {
            note.convertFalseCreatingUserStatus();
            return getSuccessFlag();
        }
        note.convertFalseOpponentUserStatus();
        return getSuccessFlag();
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
                final ProductPost productPost =
                    productPostService.loadProductPostById(loadNoteListForm.getProductPostId());
                final ProductPostFile productPostFile =
                    productPostFileService.loadProductPostFileByProductPost(productPost);
                final String[] split = productPostFile.getImageLink().split(",");

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
        final Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            return note.get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    public List<Note> loadNotesByUserId(Long userId) {
        return noteRepository.findByUser(userServiceUtility.loadUserById(userId));
    }

    @Transactional
    protected Note saveNote(ProductPost productPost, User creatingRequestUser, User opponentUser) {
        final Note note = Note.builder()
            .productPost(productPost)
            .creatingUser(creatingRequestUser)
            .creatingUserNickname(creatingRequestUser.getNickname())
            .creatingUserUnreadCount(0)
            .opponentUser(opponentUser)
            .opponentUserNickname(opponentUser.getNickname())
            .opponentUserUnreadCount(0)
            .creatingUserStatus(true)
            .opponentUserStatus(true)
            .build();
        noteRepository.save(note);
        return note;
    }

    @Transactional
    public void updateUserUnreadCountByEnteredNote(Note note, User user) {
        note.updateUserUnreadCountByEnteredNote(user);
    }

    @Transactional
    public void deleteNotesByUser(User user) {
        noteRepository.deleteByUser(user);
    }

    private boolean validateNoteCreateRequest(
        Long creatingUserId, Long opponentUserId, Long productPostId
    ) {
        if (creatingUserId.equals(opponentUserId)) {
            throw new CustomException(DO_NOT_CREATE_YOURSELF);
        }

        Optional<Note> findNote = noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
            creatingUserId, opponentUserId, productPostId);
        return findNote.isPresent();
    }
}
