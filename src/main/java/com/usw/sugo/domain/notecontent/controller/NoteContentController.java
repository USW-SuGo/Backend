package com.usw.sugo.domain.notecontent.controller;

import com.usw.sugo.domain.notecontent.service.NoteContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

import static com.usw.sugo.domain.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    private final NoteContentService noteContentService;

    @PostMapping("/")
    public ResponseEntity<Object> sendNoteContent(@RequestBody @Valid SendNoteContentForm sendNoteContentForm) {
        noteContentService.sendContent(sendNoteContentForm);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }
}
