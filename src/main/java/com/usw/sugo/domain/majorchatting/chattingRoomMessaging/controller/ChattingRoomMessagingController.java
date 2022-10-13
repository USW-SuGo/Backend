package com.usw.sugo.domain.majorchatting.chattingRoomMessaging.controller;


import com.usw.sugo.domain.majorchatting.ChattingMessage;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.service.ChattingRoomMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class ChattingRoomMessagingController {

    private final ChattingRoomMessagingService messagingService;

    private final SimpMessageSendingOperations sendingOperations;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms() {
        return "room";
    }

    /**
     * websocket "/app/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    @SendTo("/queue/message")
    public void message(@Payload ChattingMessage message) {

        // 요청한 채팅 메세지 DB 에 저장
        messagingService.uploadMessageByMessaging(message);

        // 메세지 구독자에게 발행
        sendingOperations.convertAndSend("/chat/room"
                + message.getMessage());
    }

    /**
     * websocket "/app/chat/file"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/file")
    @SendTo("/queue/file")
    public void file(@Payload ChattingMessage message) throws IOException {

        // 요청한 채팅 파일 DB에 저장
        messagingService.uploadFileByMessaging(message);

        // 메세지 구독자에게 발행
        sendingOperations.convertAndSend("/chat/room" +
                Arrays.toString(message.getMultipartFileList()));
    }
}
