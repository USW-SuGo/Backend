package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<List<LoadNoteListForm>> loadNoteListByUserId(long requestUserId, Pageable pageable);

    void readNoteRoom(long requestUserId, long noteId);

    void updateRecentContent(long unreadUserId, long noteId, String content, String imageLink);

    void findNoteByRequestUserAndTargetUserAndProductPost(long noteRequestUserId, long targetUserId, long productPostId);
}
