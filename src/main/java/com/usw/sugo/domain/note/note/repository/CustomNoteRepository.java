package com.usw.sugo.domain.note.note.repository;

import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.note.note.dto.NoteResponseDto.LoadNoteMessageForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();
    List<LoadNoteListForm> loadChattingRoomListByUserId(long userId, Pageable pageable);
    List<LoadNoteForm> loadChattingRoomFormByRoomId(long roomId);
    List<LoadNoteMessageForm> loadChattingRoomMessageFormByRoomId(long roomId, Pageable pageable);
    List<LoadNoteFileForm> loadChattingRoomFileFormByRoomId(long roomId, Pageable pageable);
}
