package com.usw.sugo.domain.note.note.controller;

import com.usw.sugo.domain.note.note.dto.NoteRequestDto.CreateNoteRequestForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.OK;

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
    public Stream<LoadNoteListForm> loadAllNoteListByUserId(Pageable pageable, @AuthenticationPrincipal User user) {
        return noteService.executeLoadAllNotes(user, pageable);
    }
}
