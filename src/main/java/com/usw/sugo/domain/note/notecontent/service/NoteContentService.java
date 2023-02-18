package com.usw.sugo.domain.note.notecontent.service;

import static com.usw.sugo.global.apiresult.ApiResult.SUCCESS;
import static com.usw.sugo.global.apiresult.ApiResultFactory.getSuccessFlag;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.aws.s3.AwsS3ServiceNote;
import com.usw.sugo.global.util.imagelinkfiltering.ImageLinkCharacterFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteContentService {

    private final NoteContentRepository noteContentRepository;
    private final NoteService noteService;
    private final UserServiceUtility userServiceUtility;
    private final ImageLinkCharacterFilter imageLinkCharacterFilter;
    private final AwsS3ServiceNote awsS3ServiceNote;

    private final Map<String, Boolean> successFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};

    @Transactional
    public List<Object> executeLoadAllContentsByNoteId(User requestUser, Long noteId,
        Pageable pageable) {
        noteService.updateUserUnreadCountByEnteredNote(noteService.loadNoteByNoteId(noteId),
            requestUser);
        List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", requestUser.getId());
        }});
        result.add(loadNoteAllContentForms(noteId, pageable));
        return result;
    }

    @Transactional
    public Map<String, Boolean> executeSendNoteContent(Long noteId, String message, Long senderId,
        Long receiverId) {
        User sender = userServiceUtility.loadUserById(senderId);
        User receiver = userServiceUtility.loadUserById(receiverId);
        Note note = noteService.loadNoteBySenderAndNoteId(sender, noteId);
        saveNoteContent(note, message, sender, receiver);
        noteService.updateRecentContent(note, message);
        noteService.updateUserUnreadCountBySendMessage(note, receiver);
        return successFlag;
    }

    private List<LoadNoteAllContentForm> loadNoteAllContentForms(Long noteId, Pageable pageable) {
        return noteContentRepository.loadAllNoteContentByNoteId(noteId, pageable);
    }


    private void saveNoteContent(Note note, String message, User sender, User receiver) {
        NoteContent noteContent = NoteContent.builder()
            .note(note)
            .message(message)
            .sender(sender)
            .receiver(receiver)
            .createdAt(LocalDateTime.now())
            .build();
        noteContentRepository.save(noteContent);
    }


    // -------------------------------------------------------------------------------------- //
    @Transactional
    public Map<String, Boolean> saveNoteFile(
        Long noteId, Long senderId, Long receiverId, MultipartFile[] multipartFiles) {
        List<String> imageLinks = awsS3ServiceNote.uploadS3ByNote(multipartFiles, noteId);
        NoteContent noteFile = NoteContent.builder()
            .note(noteService.loadNoteByNoteId(noteId))
            .sender(userServiceUtility.loadUserById(senderId))
            .receiver(userServiceUtility.loadUserById(receiverId))
            .imageLink(imageLinks.toString())
            .createdAt(LocalDateTime.now())
            .build();
        noteContentRepository.save(noteFile);
        return getSuccessFlag();
    }

    @Transactional
    public void deleteByNote(Note note) {
        awsS3ServiceNote.deleteS3ByNoteFile();
    }
}
