package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.infrastructure.aws.s3.AwsS3ServiceNote;
import com.usw.sugo.global.infrastructure.fcm.FcmMessage;
import com.usw.sugo.global.infrastructure.fcm.service.FcmPushService;
import com.usw.sugo.global.util.imagelinkfiltering.ImageLinkCharacterFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteContentService {

    private final FcmPushService fcmPushService;
    private final NoteContentRepository noteContentRepository;
    private final UserServiceUtility userServiceUtility;
    private final ImageLinkCharacterFilter imageLinkCharacterFilter;
    private final AwsS3ServiceNote awsS3ServiceNote;

    private final String fixedPushAlarmBodyByImage = "사진을 보냈습니다.";

    @Transactional
    public List<Object> executeLoadAllContentsByNoteId(
        User requestUser, Long noteId, Pageable pageable
    ) {

        final List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", requestUser.getId());
        }});
        result.add(loadNoteAllContentForm(noteId, pageable));
        return result;
    }

    @Transactional
    public String executeSendNoteContent(
        Note note, String message, Long senderId, Long receiverId
    ) {

        final User sender = userServiceUtility.loadUserById(senderId);
        final User receiver = userServiceUtility.loadUserById(receiverId);

        saveNoteContentByText(note, message, sender, receiver);
        note.updateRecentContent(message);
        note.updateUserUnreadCountBySendMessage(sender);

        final String fixedPushAlarmTitle = sender.getNickname();

        fcmPushService.sendPushNotification(
            new FcmMessage(receiver, fixedPushAlarmTitle, message), note.getId()
        );
        return message;
    }

    @Transactional
    public String executeSendNoteContentWithFile(
        Note note, MultipartFile[] multipartFiles, Long senderId, Long receiverId
    ) {

        final User sender = userServiceUtility.loadUserById(senderId);
        final User receiver = userServiceUtility.loadUserById(receiverId);

        final List<String> imageLinks = awsS3ServiceNote.uploadS3ByNote(
            multipartFiles, note.getId()
        );
        saveNoteContentByFile(note, imageLinks, sender, receiver);
        note.updateRecentContent(fixedPushAlarmBodyByImage);
        note.updateUserUnreadCountBySendMessage(sender);

        final String fixedPushAlarmTitle = sender.getNickname();

        fcmPushService.sendPushNotification(
            new FcmMessage(receiver, fixedPushAlarmTitle, fixedPushAlarmBodyByImage), note.getId()
        );
        return imageLinks.get(0);
    }

    private List<LoadNoteAllContentForm> loadNoteAllContentForm(Long noteId, Pageable pageable) {
        final List<LoadNoteAllContentForm> loadNoteAllContentsForm =
            noteContentRepository.loadAllNoteContentByNoteId(noteId, pageable);
        for (LoadNoteAllContentForm loadNoteAllContentForm : loadNoteAllContentsForm) {
            imageLinkCharacterFilter.filterImageLink(loadNoteAllContentForm);
        }
        return loadNoteAllContentsForm;
    }

    private List<NoteContent> loadAllNoteContentsByNote(Note note) {
        return noteContentRepository.findByNote(note);
    }

    @Transactional
    public void saveNoteContentByText(
        Note note, String message, User sender, User receiver
    ) {

        NoteContent noteContent = NoteContent.builder()
            .note(note)
            .message(message)
            .sender(sender)
            .receiver(receiver)
            .createdAt(LocalDateTime.now())
            .build();
        noteContentRepository.save(noteContent);
    }

    @Transactional
    public void saveNoteContentByFile(
        Note note, List<String> imageLinks, User sender, User receiver
    ) {

        NoteContent noteContent = NoteContent.builder()
            .note(note)
            .sender(sender)
            .receiver(receiver)
            .imageLink(imageLinks.toString())
            .createdAt(LocalDateTime.now())
            .build();
        noteContentRepository.save(noteContent);
    }

    @Transactional
    public void deleteNoteContentsByNotes(List<Note> notes) {
        for (Note note : notes) {
            noteContentRepository.deleteByNote(note);
            awsS3ServiceNote.deleteS3ByNoteContents(loadAllNoteContentsByNote(note));
        }
    }

    @Transactional
    public void deleteByNote(Note note) {
        noteContentRepository.deleteByNote(note);
        awsS3ServiceNote.deleteS3ByNoteContents(loadAllNoteContentsByNote(note));
    }

    @Transactional
    public void deleteNoteContentsByUser(User user) {
        final List<NoteContent> noteContents = noteContentRepository.findByUser(user);
        awsS3ServiceNote.deleteS3ByNoteContents(noteContents);
        for (NoteContent noteContent : noteContents) {
            noteContentRepository.deleteByNoteContent(noteContent);
        }
    }
}
