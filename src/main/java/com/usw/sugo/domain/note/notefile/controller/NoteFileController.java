package com.usw.sugo.domain.note.notefile.controller;


import com.usw.sugo.domain.note.notefile.dto.NoteFileRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notefile.service.NoteFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-file")
public class NoteFileController {

    private final NoteFileService noteFileService;

    @PostMapping("/")
    public ResponseEntity<Object> sendNoteContent(
            @RequestBody @Valid SendNoteFileForm sendNoteFileForm, MultipartFile[] multipartList) throws IOException {

        noteFileService.sendFile(sendNoteFileForm, multipartList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }
}
