package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteContentRepository {

    List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(Long noteId, Pageable pageable);
}
