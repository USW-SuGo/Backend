package com.usw.sugo.domain.note.notefile.controller;


import com.usw.sugo.domain.note.notefile.dto.NoteFileRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notefile.service.NoteFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-file")
public class NoteFileController {

    private final NoteFileService noteFileService;

    @ResponseStatus(OK)
    @PostMapping("/")
    public Map<String, Boolean> sendNoteContent(
            @RequestBody @Valid SendNoteFileForm sendNoteFileForm, MultipartFile[] multipartForms) throws IOException {

        noteFileService.saveNoteFile(
                sendNoteFileForm.getNoteId(),
                sendNoteFileForm.getSenderId(),
                sendNoteFileForm.getReceiverId(),
                multipartForms);
        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
