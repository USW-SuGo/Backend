package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomNoteContentRepository {

    List<LoadNoteAllContentForm> loadAllNoteContentByNoteId(Long noteId, Pageable pageable);
}
