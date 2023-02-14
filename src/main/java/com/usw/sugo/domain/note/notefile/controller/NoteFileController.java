package com.usw.sugo.domain.note.notefile.controller;


import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.note.notefile.controller.dto.NoteFileRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notefile.service.NoteFileService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-file")
public class NoteFileController {

    private final NoteFileService noteFileService;

    @ResponseStatus(OK)
    @PostMapping("/")
    public Map<String, Boolean> sendNoteContent(
        @RequestBody @Valid SendNoteFileForm sendNoteFileForm, MultipartFile[] multipartForms) {
        return noteFileService.saveNoteFile(sendNoteFileForm.getNoteId(),
            sendNoteFileForm.getSenderId(),
            sendNoteFileForm.getReceiverId(), multipartForms);
    }
}
