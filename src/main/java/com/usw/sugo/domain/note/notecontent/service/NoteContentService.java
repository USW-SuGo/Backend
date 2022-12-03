package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.entity.NoteContent;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteContentService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteContentRepository noteContentRepository;

    public void sendContent(SendNoteContentForm sendNoteContentForm) {
        NoteContent noteContent = NoteContent.builder()
                .noteId(noteRepository.findById(sendNoteContentForm.getNoteId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOTE_NOT_FOUNDED)))
                .message(sendNoteContentForm.getMessage())
                .sender(userRepository.findById(sendNoteContentForm.getSenderId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST)))
                .receiver(userRepository.findById(sendNoteContentForm.getReceiverId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST)))
                .createdAt(LocalDateTime.now())
                .build();

        noteContentRepository.save(noteContent);

        long unreadUserId = sendNoteContentForm.getReceiverId();
        noteRepository.updateRecentContent(unreadUserId, sendNoteContentForm.getNoteId(),
                sendNoteContentForm.getMessage(), "");
    }

    public void deleteContent(User requestUser) {
        noteContentRepository.deleteBySender(requestUser);
        noteContentRepository.deleteByReceiver(requestUser);
    }
}
