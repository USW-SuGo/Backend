package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.note.Note;
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
    private final UserServiceUtility userServiceUtility;
    private final ImageLinkCharacterFilter imageLinkCharacterFilter;
    private final AwsS3ServiceNote awsS3ServiceNote;

    @Transactional
    public List<Object> executeLoadAllContentsByNoteId(
        User requestUser, Long noteId, Pageable pageable) {
        List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", requestUser.getId());
        }});
        result.add(loadNoteAllContentForm(noteId, pageable));
        return result;
    }

    @Transactional
    public String executeSendNoteContent(
        Note note, String message, Long senderId, Long receiverId) {
        User sender = userServiceUtility.loadUserById(senderId);
        User receiver = userServiceUtility.loadUserById(receiverId);
        saveNoteContentByText(note, message, sender, receiver);
        return message;
    }

    @Transactional
    public String executeSendNoteContentWithFile(
        Note note, MultipartFile[] multipartFiles, Long senderId, Long receiverId) {
        List<String> imageLinks = awsS3ServiceNote.uploadS3ByNote(multipartFiles, note.getId());
        User sender = userServiceUtility.loadUserById(senderId);
        User receiver = userServiceUtility.loadUserById(receiverId);
        saveNoteContentByFile(note, imageLinks, sender, receiver);
        return imageLinks.get(0);
    }

    private List<LoadNoteAllContentForm> loadNoteAllContentForm(Long noteId, Pageable pageable) {
        List<LoadNoteAllContentForm> loadNoteAllContentsForm =
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
    protected void saveNoteContentByText(Note note, String message, User sender, User receiver) {
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
    protected void saveNoteContentByFile(Note note, List<String> imageLinks, User sender,
        User receiver) {
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

}
