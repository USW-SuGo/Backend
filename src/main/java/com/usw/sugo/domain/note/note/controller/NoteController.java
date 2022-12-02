package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequest;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.productpost.entity.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.usw.sugo.global.exception.ErrorCode.DO_NOT_CREATE_YOURSELF;
import static com.usw.sugo.global.exception.ErrorCode.NOTE_ALREADY_CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final ProductPostRepository productPostRepository;
    private final NoteRepository noteRepository;
    private final NoteContentRepository noteContentRepository;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;

    @PostMapping
    public ResponseEntity<Object> createRoom(
            @RequestHeader String authorization, @RequestBody CreateNoteRequest createNoteRequest) {

        long creatingRequestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        ProductPost productPost = productPostRepository.findById(createNoteRequest.getProductPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_BAD_REQUEST));
        User creatingRequestUser = userRepository.findById(creatingRequestUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
        User opponentUser = userRepository.findById(createNoteRequest.getOpponentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
        Optional<Note> findingTargetNote = noteRepository.findNoteByRequestUserAndTargetUserAndProductPost(
                creatingRequestUserId, opponentUser.getId(), productPost.getId());

        if (findingTargetNote.isPresent()) {
            throw new CustomException(NOTE_ALREADY_CREATED);
        }

        if (creatingRequestUser.equals(opponentUser)) {
            throw new CustomException(DO_NOT_CREATE_YOURSELF);
        }

        Note note = Note.builder()
                .productPost(productPost)
                .creatingUserId(creatingRequestUser)
                .creatingUserNickname(creatingRequestUser.getNickname())
                .creatingUserUnreadCount(0)
                .opponentUserId(opponentUser)
                .opponentUserNickname(opponentUser.getNickname())
                .opponentUserUnreadCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(creatingRequestUser.getId(), opponentUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<String, Long>() {{
                    put("noteId", note.getId());
                }});
    }

    @GetMapping("/list")
    public ResponseEntity<Object> loadAllNoteListByUserId(
            @RequestHeader String authorization, Pageable pageable) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        User requestUser = userRepository.findById(userId).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXIST));

        List<List<LoadNoteListForm>> noteListResult =
                noteRepository.loadNoteListByUserId(requestUser.getId(), pageable);

        List<LoadNoteListForm> loadNoteListFormRequestUserIsCreatingNote = noteListResult.get(0);
        List<LoadNoteListForm> loadNoteListFormsRequestUserIsCreatedNote = noteListResult.get(1);

        List<LoadNoteListForm> tempResult = new ArrayList<>();
        tempResult.addAll(loadNoteListFormRequestUserIsCreatingNote);
        tempResult.addAll(loadNoteListFormsRequestUserIsCreatedNote);

        Stream<LoadNoteListForm> finalResult = tempResult
                .stream()
                .sorted(Comparator.comparing(LoadNoteListForm::getRecentChattingDate)
                        .reversed());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(finalResult);
    }

    @GetMapping("/")
    public ResponseEntity<Object> loadAllNoteContentByRoomId(
            @RequestHeader String authorization, @RequestParam Long noteId, Pageable pageable) {

        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        User requestUser = userRepository.findById(userId).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXIST));
        noteRepository.readNoteRoom(requestUser.getId(), noteId);
        List<LoadNoteAllContentForm> loadNoteAllContentForms =
                noteContentRepository.loadNoteRoomAllContentByRoomId(requestUser.getId(), noteId, pageable);

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadNoteAllContentForms) {
            loadNoteAllContentForm.setRequestUserId(requestUser.getId());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loadNoteAllContentForms);
    }
}
