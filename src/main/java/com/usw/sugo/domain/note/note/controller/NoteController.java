package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequestForm;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.productpost.productpost.ProductPost;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final JwtResolver jwtResolver;
    private final NoteControllerValidator noteControllerValidator;
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Object> createRoom(
            @RequestHeader String authorization,
            @RequestBody CreateNoteRequestForm createNoteRequestForm) {

        User creatingRequestUser = validateAndExtractUser(authorization);
        User opponentUser = noteControllerValidator
                .validateUser(createNoteRequestForm.getOpponentUserId());

        ProductPost productPost = noteControllerValidator
                .validateProductPost(createNoteRequestForm.getProductPostId());

        noteControllerValidator.validateCreatingNoteRoom(
                creatingRequestUser.getId(), opponentUser.getId(), productPost.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("noteId", noteService.makeNote(productPost, creatingRequestUser, opponentUser));
                }});
    }

    @GetMapping("/list")
    public ResponseEntity<Object> loadAllNoteListByUserId(
            @RequestHeader String authorization,
            Pageable pageable) {

        User requestUser = validateAndExtractUser(authorization);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.loadNoteList(requestUser, pageable));
    }

    private User validateAndExtractUser(String authorization) {
        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return noteControllerValidator.validateUser(userId);
    }
}
