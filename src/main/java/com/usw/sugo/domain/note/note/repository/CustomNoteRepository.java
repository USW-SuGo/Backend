package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteMessageForm;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();

    List<Object> loadNoteListByUserId(long requestUserId, long opponentUserIdx, Pageable pageable);

    List<LoadNoteMessageForm> loadNoteMessageFormByRoomId(long roomId, Pageable pageable);

    List<LoadNoteFileForm> loadNoteFileFormByRoomId(long roomId, Pageable pageable);

    void updateRecentContent(long roomId, String content, String imageLink);
}
