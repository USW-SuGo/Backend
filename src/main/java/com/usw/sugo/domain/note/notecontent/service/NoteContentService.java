package com.usw.sugo.domain.note.notecontent.service;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.repository.NoteContentRepository;
import com.usw.sugo.domain.note.notefile.service.NoteFileService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.util.imagelinkfiltering.ImageLinkCharacterFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.usw.sugo.global.apiresult.ApiResult.SUCCESS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteContentService {

    private final NoteContentRepository noteContentRepository;
    private final NoteService noteService;
    private final NoteFileService noteFileService;
    private final UserServiceUtility userServiceUtility;
    private final ImageLinkCharacterFilter imageLinkCharacterFilter;

    private final Map<String, Boolean> successFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};

    @Transactional
    public List<Object> executeLoadAllContentsByNoteId(User requestUser, Long noteId, Pageable pageable) {
        noteService.updateUserUnreadCountByEnteredNote(noteService.loadNoteByNoteId(noteId), requestUser);
        List<Object> result = new ArrayList<>();
        result.add(new HashMap<>() {{
            put("requestUserId", requestUser.getId());
        }});
        result.add(loadNoteAllContentForms(noteId, pageable, requestUser));
        return result;
    }

    @Transactional
    public Map<String, Boolean> executeSendNoteContent(Long noteId, String message, Long senderId, Long receiverId) {
        User sender = userServiceUtility.loadUserById(senderId);
        User receiver = userServiceUtility.loadUserById(receiverId);
        Note note = noteService.loadNoteBySenderAndNoteId(sender, noteId);
        saveNoteContent(note, message, sender, receiver);
        noteService.updateRecentContent(note, message);
        noteService.updateUserUnreadCountBySendMessage(note, receiver);
        return successFlag;
    }

    private List<LoadNoteAllContentForm> loadNoteAllContentForms(Long noteId, Pageable pageable, User requestUser) {
        List<LoadNoteAllContentForm> loadAllNoteContentByNoteId =
                noteContentRepository.loadAllNoteContentByNoteId(noteId, pageable);

        List<LoadNoteAllContentForm> loadAllNoteFileByNoteId =
            noteFileService.loadAllNoteFileByNoteId(noteId, pageable);

        List<LoadNoteAllContentForm> results = new ArrayList<>();
        results.addAll(loadAllNoteContentByNoteId);
        results.addAll(loadAllNoteFileByNoteId);

        results.sort(makeCustomComparator());

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadAllNoteContentByNoteId) {
            loadNoteAllContentForm = imageLinkCharacterFilter.filterImageLink(loadNoteAllContentForm);
        }

        for (LoadNoteAllContentForm loadNoteAllContentForm : loadAllNoteFileByNoteId) {
            loadNoteAllContentForm = imageLinkCharacterFilter.filterImageLink(loadNoteAllContentForm);
        }

        Collections.reverse(results);

        return results;
    }

    private Comparator<LoadNoteAllContentForm> makeCustomComparator() {
       return new Comparator<LoadNoteAllContentForm>() {
            @Override
            public int compare(LoadNoteAllContentForm o1, LoadNoteAllContentForm o2) {
                LocalDateTime o1Time = o1.getMessageCreatedAt() != null ? o1.getMessageCreatedAt() : o1.getFileCreatedAt();
                LocalDateTime o2Time = o2.getMessageCreatedAt() != null ? o2.getMessageCreatedAt() : o2.getFileCreatedAt();
                return o1Time.compareTo(o2Time);
            }
        };
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
}
