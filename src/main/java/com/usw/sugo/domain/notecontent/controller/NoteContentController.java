package com.usw.sugo.domain.notecontent.controller;

import com.usw.sugo.domain.notecontent.service.NoteContentService;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

import static com.usw.sugo.domain.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    private final JwtResolver jwtResolver;
    private final NoteContentControllerValidator noteContentControllerValidator;
    private final NoteContentService noteContentService;

    @GetMapping("/{noteId}")
    public ResponseEntity<Object> loadAllNoteContentByRoomId(
            @RequestHeader String authorization, @PathVariable Long noteId,
            Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteContentService
                        .loadAllContentByNoteId(validateAndExtractUser(authorization), noteId, pageable));
    }

    @PostMapping("/")
    public ResponseEntity<Object> sendNoteContent(
            @RequestBody @Valid SendNoteContentForm sendNoteContentForm) {
        noteContentControllerValidator.validateUser(sendNoteContentForm.getSenderId());
        noteContentControllerValidator.validateUser(sendNoteContentForm.getReceiverId());
        noteContentService.sendContent(sendNoteContentForm);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    private User validateAndExtractUser(String authorization) {
        long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return noteContentControllerValidator.validateUser(userId);
    }
}
