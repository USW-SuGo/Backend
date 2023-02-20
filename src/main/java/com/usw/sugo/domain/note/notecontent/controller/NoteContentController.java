package com.usw.sugo.domain.note.notecontent.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.apiresult.ApiResultFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note-content")
public class NoteContentController {

    private final UserServiceUtility userServiceUtility;
    private final NoteContentService noteContentService;
    private final NoteService noteService;

    @ResponseStatus(OK)
    @GetMapping("/{noteId}")
    public List<Object> loadAllNoteContentsByRoomId(
        @PathVariable Long noteId,
        Pageable pageable,
        @AuthenticationPrincipal User user) {
        noteService.updateUserUnreadCountByEnteredNote(noteService.loadNoteByNoteId(noteId), user);
        return noteContentService.executeLoadAllContentsByNoteId(user, noteId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping("/text")
    public Map<String, Boolean> sendNoteContent(
        @RequestBody SendNoteContentForm sendNoteContentForm,
        @AuthenticationPrincipal User user) {
        User sender = userServiceUtility.loadUserById(user.getId());
        Note note = noteService.loadNoteByNoteId(sendNoteContentForm.getNoteId());
        noteContentService.executeSendNoteContent(
            note,
            sendNoteContentForm.getMessage(),
            sender.getId(),
            sendNoteContentForm.getReceiverId()
        );

        return ApiResultFactory.getSuccessFlag();
    }

    @ResponseStatus(OK)
    @PostMapping("/file")
    public Map<String, Boolean> sendNoteFile(
        SendNoteFileForm sendNoteFileForm,
        @RequestBody MultipartFile[] multipartFileList,
        @AuthenticationPrincipal User user) {

        User sender = userServiceUtility.loadUserById(user.getId());
        Note note = noteService.loadNoteByNoteId(sendNoteFileForm.getNoteId());
        noteContentService.executeSendNoteContentWithFile(
            note,
            multipartFileList,
            sender.getId(),
            sendNoteFileForm.getReceiverId()
        );
        return ApiResultFactory.getSuccessFlag();
    }
}
