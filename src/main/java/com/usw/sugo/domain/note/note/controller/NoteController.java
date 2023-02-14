package com.usw.sugo.domain.note.note.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequestForm;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.user.user.User;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    @ResponseStatus(OK)
    @PostMapping
    public Map<String, Long> createRoom(
        @RequestBody CreateNoteRequestForm createNoteRequestForm,
        @AuthenticationPrincipal User user) {
        return noteService.executeCreatingRoom(
            user.getId(),
            createNoteRequestForm.getOpponentUserId(),
            createNoteRequestForm.getProductPostId());
    }

    @GetMapping("/list")
    public List<Object> loadAllNoteListByUserId(Pageable pageable,
        @AuthenticationPrincipal User user) {
        return noteService.executeLoadAllNotes(user, pageable);
    }
}
