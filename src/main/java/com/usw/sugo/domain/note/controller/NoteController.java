package com.usw.sugo.domain.note.controller;

import com.usw.sugo.domain.note.dto.NoteRequestDto.CreateNoteRequestForm;
import com.usw.sugo.domain.note.service.NoteService;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.global.baseresponseform.BaseResponseCode;
import com.usw.sugo.global.baseresponseform.BaseResponseForm;
import com.usw.sugo.global.baseresponseform.BaseResponseMessage;
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

        ProductPost productPost = noteControllerValidator
                .validateProductPost(createNoteRequestForm.getProductPostId());
        User creatingRequestUser = validateAndExtractUser(authorization);
        User opponentUser = noteControllerValidator
                .validateUser(createNoteRequestForm.getOpponentUserId());

        noteControllerValidator.validateCreatingNoteRoom(
                creatingRequestUser.getId(), opponentUser.getId(), productPost.getId());

        BaseResponseForm baseResponseForm = new BaseResponseForm().build(
                BaseResponseCode.SUCCESS.getCode(),
                BaseResponseMessage.SUCCESS.getMessage(),
                new HashMap<>() {{
                    put("noteId", noteService.makeNote(productPost, creatingRequestUser, opponentUser));
                }});

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(baseResponseForm);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> loadAllNoteListByUserId(
            @RequestHeader String authorization,
            Pageable pageable) {

        User requestUser = validateAndExtractUser(authorization);

        BaseResponseForm baseResponseForm = new BaseResponseForm().build(
                BaseResponseCode.SUCCESS.getCode(),
                BaseResponseMessage.SUCCESS.getMessage(),
                noteService.loadNoteList(requestUser, pageable));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(baseResponseForm);
    }

    private User validateAndExtractUser(String authorization) {
        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return noteControllerValidator.validateUser(userId);
    }
}
