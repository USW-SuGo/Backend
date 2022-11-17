package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteMessageForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<Object> loadNoteListByUserId(long requestUserId, long opponentUserIdx, Pageable pageable);

    List<LoadNoteMessageForm> loadNoteMessageFormByRoomId(long requestUserId, long roomId, Pageable pageable);

    List<LoadNoteFileForm> loadNoteFileFormByRoomId(long requestUserId, long roomId, Pageable pageable);

    void updateRecentContent(long unreadUserId, long roomId, String content, String imageLink);

    void findByNoteRequestUserAndTargetUserAndProductPost(long noteRequestUserId, long targetUserId, long productPostId);
}
