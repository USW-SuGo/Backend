package com.usw.sugo.domain.chatting.room.repository;

import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingListForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomFileForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomForm;
import com.usw.sugo.domain.chatting.room.dto.ChattingRoomResponseDto.LoadChattingRoomMessageForm;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChattingRoomRepository {

    void deleteBeforeWeek();
    List<LoadChattingListForm> loadChattingRoomListByUserId(long userId, Pageable pageable);
    List<LoadChattingRoomForm> loadChattingRoomFormByRoomId(long roomId);
    List<LoadChattingRoomMessageForm> loadChattingRoomMessageFormByRoomId(long roomId, Pageable pageable);
    List<LoadChattingRoomFileForm> loadChattingRoomFileFormByRoomId(long roomId, Pageable pageable);

}
