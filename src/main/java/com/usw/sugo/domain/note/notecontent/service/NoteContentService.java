package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.usw.sugo.domain.ApiResult.SUCCESS;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteContentService {

    private final NoteContentRepository noteContentRepository;
    private final NoteService noteService;
    private final UserServiceUtility userServiceUtility;

    private final Map<String, Boolean> successFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};

    public Map<String, Boolean> executeSendNoteContent(Long noteId, String message, Long senderId, Long receiverId) {
        User sender = userServiceUtility.loadUserById(senderId);
        User receiver = userServiceUtility.loadUserById(receiverId);
        Note note = noteService.loadNoteBySenderAndNoteId(sender, noteId);

        NoteContent noteContent = NoteContent.builder()
                .note(note)
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .build();
        noteContentRepository.save(noteContent);
        noteService.updateRecentContent(note, message);
        noteService.updateUnreadCountByNoteAndReceiver(note, receiver);

        return successFlag;
    }

    public List<LoadNoteAllContentForm> loadAllContentByNoteId(User requestUser, Long noteId, Pageable pageable) {
        noteService.readNoteRoom(noteId, requestUser);
        List<LoadNoteAllContentForm> loadNoteAllContentForms = noteContentRepository
                .loadNoteRoomAllContentByRoomId(noteId, pageable);

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadNoteAllContentForms) {
            loadNoteAllContentForm.setRequestUserId(requestUser.getId());
        }
        return loadNoteAllContentForms;
    }

    public void updateRecentContent() {

    }

    private User extractUnreadUserIsCreatingUserOrOpponentUser(Long receiverId, Note note) {
        if (note.getCreatingUser().getId().equals(receiverId)) {
            return note.getCreatingUser();
        }
        return note.getOpponentUser();
    }
}
