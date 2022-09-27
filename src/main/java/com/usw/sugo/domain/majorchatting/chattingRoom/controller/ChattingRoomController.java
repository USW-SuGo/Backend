package com.usw.sugo.domain.majorchatting.chattingRoom.controller;

import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majoruser.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingRoomController {
    private final ChattingRoomRepository chattingRoomRepository;

    @PostMapping
    public void createRoom(@RequestBody ChattingRoomRequest.CreateChattingRoomRequest createChattingRoomRequest,
                                   User sender, User receiver) {
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .roomValue(UUID.randomUUID().toString())
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        chattingRoomRepository.save(chattingRoom);
    }

    @GetMapping
    public List<ChattingRoom> findAllRoom() {
        return chattingRoomRepository.findAll();
    }


}
