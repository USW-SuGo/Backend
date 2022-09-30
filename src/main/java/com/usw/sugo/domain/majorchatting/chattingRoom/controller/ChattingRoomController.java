package com.usw.sugo.domain.majorchatting.chattingRoom.controller;

import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChatRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.RoomRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingRoomController {
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;
    
    // 채팅방 만들기
    @PostMapping("/room")
    public ResponseEntity<Object> createRoom(@RequestBody RoomRequest request) {

        User sender = userRepository.findById(request.getSenderId()).get();
        User receiver = userRepository.findById(request.getReceiverId()).get();

        ChattingRoom chattingRoom = ChattingRoom.builder()
                .roomValue(UUID.randomUUID().toString())
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        chattingRoomRepository.save(chattingRoom);

        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String, Long>() {{
            put("roomId", chattingRoom.getId());
        }});
    }
}
