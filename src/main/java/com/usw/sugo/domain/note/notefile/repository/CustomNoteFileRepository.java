package com.usw.sugo.domain.note.notefile.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteAllContentForm;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomNoteFileRepository {

    List<LoadNoteAllContentForm> loadNoteRoomAllContentByRoomId(Long noteId, Pageable pageable);

}
