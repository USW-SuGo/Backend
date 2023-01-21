package com.usw.sugo.domain.notecontent.service;

import com.usw.sugo.domain.note.repository.NoteRepository;
import com.usw.sugo.domain.notecontent.NoteContent;
import com.usw.sugo.domain.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.global.entityvalidator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteContentService {
    private final EntityValidator entityValidator;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteContentRepository noteContentRepository;

    public void sendContent(SendNoteContentForm sendNoteContentForm) {
        NoteContent noteContent = NoteContent.builder()
                .noteId(entityValidator.validateNote(sendNoteContentForm.getNoteId()))
                .message(sendNoteContentForm.getMessage())
                .sender(userRepository.findById(sendNoteContentForm.getSenderId()).get())
                .receiver(userRepository.findById(sendNoteContentForm.getReceiverId()).get())
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
