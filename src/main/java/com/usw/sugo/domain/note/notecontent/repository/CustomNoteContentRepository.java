package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteRoomContentForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteContentRepository {

    List<LoadNoteRoomContentForm> loadNoteRoomAllContentByRoomId(long requestUserId, long noteId, Pageable pageable);
}
