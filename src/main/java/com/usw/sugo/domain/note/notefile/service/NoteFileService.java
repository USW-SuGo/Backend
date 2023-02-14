package com.usw.sugo.domain.note.notefile.service;

import static com.usw.sugo.global.apiresult.ApiResultFactory.getSuccessFlag;
import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_UPLOAD_EXCEPTION;
import static com.usw.sugo.global.exception.ExceptionType.NOTE_NOT_FOUNDED;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notefile.NoteFile;
import com.usw.sugo.domain.note.notefile.repository.NoteFileRepository;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.aws.s3.AwsS3ServiceNote;
import com.usw.sugo.global.exception.CustomException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteFileService {

    private final UserServiceUtility userServiceUtility;
    private final NoteService noteService;
    private final NoteFileRepository noteFileRepository;
    private final AwsS3ServiceNote awsS3ServiceNote;

    public NoteFile loadNoteFileByNote(Note note) {
        Optional<NoteFile> noteFile = noteFileRepository.findByNote(note);
        if (noteFile.isPresent()) {
            return noteFile.get();
        }
        throw new CustomException(NOTE_NOT_FOUNDED);
    }

    @Transactional
    public Map<String, Boolean> saveNoteFile(
        Long noteId, Long senderId, Long receiverId, MultipartFile[] multipartFiles) {
        List<String> imageLinks = null;
        try {
            imageLinks = awsS3ServiceNote.uploadS3ByNote(multipartFiles, noteId);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
        }
        NoteFile noteFile = NoteFile.builder()
            .note(noteService.loadNoteByNoteId(noteId))
            .sender(userServiceUtility.loadUserById(senderId))
            .receiver(userServiceUtility.loadUserById(receiverId))
            .imageLink(imageLinks.toString())
            .createdAt(LocalDateTime.now())
            .build();
        noteFileRepository.save(noteFile);
        return getSuccessFlag();
    }

    @Transactional
    public void deleteByNote(Note note) {
        awsS3ServiceNote.deleteS3ByNote(loadNoteFileByNote(note));
        noteFileRepository.deleteByNote(note);
    }
}
