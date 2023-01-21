package com.usw.sugo.domain.note.controller;

import com.usw.sugo.domain.note.dto.NoteRequestDto.CreateNoteRequestForm;
import com.usw.sugo.domain.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.note.service.NoteService;
import com.usw.sugo.domain.notecontent.controller.NoteContentControllerValidator;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.global.baseresponseform.BaseResponseForm;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final NoteRepository noteRepository;
    private final JwtResolver jwtResolver;
    private final NoteContentControllerValidator noteContentControllerValidator;
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Object> createRoom(
            @RequestHeader String authorization,
            @RequestBody CreateNoteRequestForm createNoteRequestForm) {
        long creatingRequestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        ProductPost productPost = noteContentControllerValidator.validateProductPost(createNoteRequestForm.getProductPostId());
        User creatingRequestUser = noteContentControllerValidator.validateUser(creatingRequestUserId);
        User opponentUser = noteContentControllerValidator.validateUser(createNoteRequestForm.getOpponentUserId());

        noteContentControllerValidator.validateCreatingNoteRoom(creatingRequestUserId, opponentUser.getId(), productPost.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<String, Long>() {{
                    put("noteId", noteService.makeNote(productPost, creatingRequestUser, opponentUser));
                }});
    }

    @GetMapping("/list")
    public ResponseEntity<Object> loadAllNoteListByUserId(
            @RequestHeader String authorization,
            Pageable pageable) {

        User requestUser = validateAndExtractUser(authorization);

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

        BaseResponseForm baseResponseForm = new BaseResponseForm();
                .code(new HashMap<>() {{
                    put();
                }})
                .message(new HashMap<>() {{
                    put("message", "SUCCESS");
                }})
                .data(new HashMap<>() {{
                    put("data", finalResult);
                }})
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(baseResponseForm);
    }

    private User validateAndExtractUser(String authorization) {
        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return noteContentControllerValidator.validateUser(userId);
    }
}
