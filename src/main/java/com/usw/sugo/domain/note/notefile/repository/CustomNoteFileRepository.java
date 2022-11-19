package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomFileForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteFileRepository {
    List<LoadNoteRoomFileForm> loadNoteRoomAllFileByRoomId(long requestUserId, long noteId, Pageable pageable);
}
