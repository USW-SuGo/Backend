package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<List<LoadNoteListForm>> loadNoteListByUserId(long requestUserId, Pageable pageable);

    void readNoteRoom(long requestUserId, long noteId);

    void updateRecentContent(long unreadUserId, long noteId, String content, String imageLink);

    Optional<Note> findNoteByRequestUserAndTargetUserAndProductPost(long noteRequestUserId, long targetUserId, long productPostId);
}
