package com.usw.sugo.domain.note.notecontent.controller;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import com.usw.sugo.domain.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    private final NoteContentControllerValidator noteContentControllerValidator;
    private final NoteContentService noteContentService;

    @ResponseStatus(OK)
    @GetMapping("/{noteId}")
    public List<LoadNoteAllContentForm> loadAllNoteContentByRoomId(
            @PathVariable Long noteId,
            Pageable pageable,
            @AuthenticationPrincipal User user) {
        return noteContentService.loadAllContentByNoteId(user, noteId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping("/")
    public Map<String, Boolean> sendNoteContent(
            @RequestBody SendNoteContentForm sendNoteContentForm) {
        noteContentControllerValidator.validateUser(sendNoteContentForm.getSenderId());
        noteContentControllerValidator.validateUser(sendNoteContentForm.getReceiverId());
        return noteContentService.executeSendNoteContent(
                sendNoteContentForm.getNoteId(),
                sendNoteContentForm.getMessage(),
                sendNoteContentForm.getSenderId(),
                sendNoteContentForm.getReceiverId());
    }
}
