package com.usw.sugo.domain.note.notecontent.controller;

import static com.usw.sugo.global.exception.ExceptionType.DO_NOT_SEND_YOURSELF;
import static com.usw.sugo.global.valueobject.apiresult.ApiResultFactory.getSuccessFlag;
import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.annotation.ApiLogger;
import com.usw.sugo.global.exception.CustomException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
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

@ApiLogger
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
        @AuthenticationPrincipal User user
    ) {
        noteService.initUserUnreadCountByEnteredNote(noteService.loadNoteByNoteId(noteId), user);
        return noteContentService.executeLoadAllContentsByNoteId(user, noteId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping("/text")
    public Map<String, Boolean> sendNoteContent(
        @Valid @RequestBody SendNoteContentForm sendNoteContentForm,
        @AuthenticationPrincipal User user
    ) {

        validateSendForm(user, sendNoteContentForm.getSenderId(),
            sendNoteContentForm.getReceiverId());

        User sender = userServiceUtility.loadUserById(user.getId());
        User receiver = userServiceUtility.loadUserById(sendNoteContentForm.getReceiverId());
        Note note = noteService.loadNoteByNoteId(sendNoteContentForm.getNoteId());
        noteContentService.executeSendNoteContent(
            note,
            sendNoteContentForm.getMessage(),
            sendNoteContentForm.getSenderId(),
            sendNoteContentForm.getReceiverId()
        );

        return getSuccessFlag();
    }

    @ResponseStatus(OK)
    @PostMapping("/file")
    public Map<String, Boolean> sendNoteFile(
        @Valid SendNoteFileForm sendNoteFileForm,
        @RequestBody MultipartFile[] multipartFileList,
        @AuthenticationPrincipal User user
    ) {

        validateSendForm(user, sendNoteFileForm.getSenderId(), sendNoteFileForm.getReceiverId());

        User sender = userServiceUtility.loadUserById(user.getId());
        User receiver = userServiceUtility.loadUserById(sendNoteFileForm.getReceiverId());
        Note note = noteService.loadNoteByNoteId(sendNoteFileForm.getNoteId());
        noteContentService.executeSendNoteContentWithFile(
            note,
            multipartFileList,
            user.getId(),
            sendNoteFileForm.getReceiverId()
        );
        return getSuccessFlag();
    }

    private void validateSendForm(User user, Long senderId, Long receiverId) {
        if (!user.getId().equals(senderId) || user.getId().equals(receiverId)) {
            throw new CustomException(DO_NOT_SEND_YOURSELF);
        }
    }
}
