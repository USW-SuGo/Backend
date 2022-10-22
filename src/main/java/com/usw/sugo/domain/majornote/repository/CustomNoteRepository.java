package com.usw.sugo.domain.majornote.repository;

import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteFileForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteListForm;
import com.usw.sugo.domain.majornote.dto.NoteResponseDto.LoadNoteMessageForm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNoteRepository {

    void deleteBeforeWeek();
    List<LoadNoteListForm> loadChattingRoomListByUserId(long userId, Pageable pageable);
    List<LoadNoteForm> loadChattingRoomFormByRoomId(long roomId);
    List<LoadNoteMessageForm> loadChattingRoomMessageFormByRoomId(long roomId, Pageable pageable);
    List<LoadNoteFileForm> loadChattingRoomFileFormByRoomId(long roomId, Pageable pageable);
}
