package com.usw.sugo.domain.note.notefile.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notefile.NoteFile;
import com.usw.sugo.domain.note.notefile.repository.NoteFileRepository;
import com.usw.sugo.domain.user.user.service.UserService;
import com.usw.sugo.global.aws.s3.AwsS3ServiceNote;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.global.exception.ExceptionType.NOTE_NOT_FOUNDED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteFileService {

    private final UserService userService;
    private final NoteService noteService;
    private final NoteFileRepository noteFileRepository;
    private final AwsS3ServiceNote awsS3ServiceNote;

    public NoteFile loadNoteFileByNote(Note note) {
        if (noteFileRepository.findByNote(note).isPresent()) {
            return noteFileRepository.findByNote(note).get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    @Transactional
    public List<String> saveNoteFile(
            Long noteId, Long senderId, Long receiverId, MultipartFile[] multipartFiles) throws IOException {

        List<String> imageLinks = awsS3ServiceNote.uploadS3ByNote(multipartFiles, noteId);
        NoteFile noteFile = NoteFile.builder()
                .note(noteService.loadNoteById(noteId))
                .sender(userService.loadUserById(senderId))
                .receiver(userService.loadUserById(receiverId))
                .imageLink(imageLinks.toString())
                .createdAt(LocalDateTime.now())
                .build();
        noteFileRepository.save(noteFile);
        return imageLinks;
    }

    @Transactional
    public void deleteByNote(Note note) {
        awsS3ServiceNote.deleteS3ByNote(loadNoteFileByNote(note));
        noteFileRepository.deleteByNote(note);
    }
}
