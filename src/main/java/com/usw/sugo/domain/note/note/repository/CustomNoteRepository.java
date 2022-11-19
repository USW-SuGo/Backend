package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<List<LoadNoteListForm>> loadNoteListByUserId(long requestUserId, Pageable pageable);

    List<LoadNoteRoomForm> loadNoteRoomAllContentByRoomId(long requestUserId, long roomId, Pageable pageable);

    void updateRecentContent(long unreadUserId, long roomId, String content, String imageLink);

    void findNoteByRequestUserAndTargetUserAndProductPost(long noteRequestUserId, long targetUserId, long productPostId);
}
