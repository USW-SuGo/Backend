package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.entity.NoteContent;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notecontent.dto.NoteContentRequestDto.SendNoteContentForm;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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
                .sender(userRepository.findById(sendNoteContentForm.getReceiverId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST)))
                .build();

        noteContentRepository.save(noteContent);

        long unreadUserId = -1;

        Optional<Note> targetNote = noteRepository.findById(sendNoteContentForm.getNoteId());
        if (targetNote.get().getCreatingUserId().getId() == sendNoteContentForm.getSenderId()) {
            unreadUserId = targetNote.get().getOpponentUserId().getId();
        } else if (targetNote.get().getCreatingUserId().getId() != sendNoteContentForm.getSenderId()) {
            unreadUserId = targetNote.get().getCreatingUserId().getId();
        }

        noteRepository.updateRecentContent(unreadUserId, sendNoteContentForm.getNoteId(),
                sendNoteContentForm.getMessage(), "");
    }
}
