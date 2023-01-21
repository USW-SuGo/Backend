package com.usw.sugo.domain.notecontent.repository;

import com.usw.sugo.domain.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteContentRepository {

    List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(long requestUserId, long noteId, Pageable pageable);
}
