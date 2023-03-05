package com.usw.sugo.domain.note.notecontent.repository;

import com.usw.sugo.domain.note.note.Note;
import com.usw.sugo.domain.note.notecontent.NoteContent;
import com.usw.sugo.domain.note.notecontent.controller.dto.NoteContentResponseDto.LoadNoteAllContentForm;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomNoteContentRepository {

    List<LoadNoteAllContentForm> loadAllNoteContentByNoteId(Long noteId, Pageable pageable);

    void deleteByNoteContent(NoteContent noteContent);

    void deleteByNote(Note note);
}
