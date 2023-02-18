package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.controller.dto.NoteResponseDto.LoadNoteListForm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<List<LoadNoteListForm>> loadNoteListByUserId(Long requestUserId, Pageable pageable);

    void readNoteRoom(Long requestUserId, Long noteId);

    void updateRecentContent(Long unreadUserId, Long noteId, String content, String imageLink);

    Optional<Note> findNoteByRequestUserAndTargetUserAndProductPost(Long noteRequestUserId,
        Long targetUserId, Long productPostId);
}
