package com.usw.sugo.domain.majorchatting.chattingRoom.repository;

import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto.LoadChattingListForm;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto.LoadChattingRoomFileForm;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto.LoadChattingRoomForm;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomResponseDto.LoadChattingRoomMessageForm;
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
