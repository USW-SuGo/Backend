package com.usw.sugo.domain.majorchatting.chattingRoomMessage.controller;

import com.usw.sugo.domain.majorchatting.ChattingRoomMessage;
import com.usw.sugo.domain.majorchatting.chattingRoomMessage.repository.ChattingRoomMessageRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chatting/message")
public class ChattingRoomMessageController {

    private final ChattingRoomMessageRepository chattingRoomMessageRepository;

    @PostMapping
    public void postMessage(User sender, User receiver, String message) {
        ChattingRoomMessage chattingRoomMessage = ChattingRoomMessage.builder()
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        chattingRoomMessageRepository.save(chattingRoomMessage);
    }
}
