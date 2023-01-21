package com.usw.sugo.domain.notecontent.controller;

import com.usw.sugo.domain.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.notecontent.repository.NoteContentRepository;
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
import java.util.List;

import static com.usw.sugo.domain.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    private final JwtResolver jwtResolver;
    private final NoteContentControllerValidator noteContentControllerValidator;
    private final NoteContentService noteContentService;
    private final NoteRepository noteRepository;
    private final NoteContentRepository noteContentRepository;

    @GetMapping("/")
    public ResponseEntity<Object> loadAllNoteContentByRoomId(
            @RequestHeader String authorization,
            @RequestParam Long noteId,
            Pageable pageable) {

        User requestUser = validateAndExtractUser(authorization);

        noteRepository.readNoteRoom(requestUser.getId(), noteId);
        List<LoadNoteAllContentForm> loadNoteAllContentForms =
                noteContentRepository.loadNoteRoomAllContentByRoomId(noteId, pageable);

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadNoteAllContentForms) {
            loadNoteAllContentForm.setRequestUserId(requestUser.getId());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loadNoteAllContentForms);
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
