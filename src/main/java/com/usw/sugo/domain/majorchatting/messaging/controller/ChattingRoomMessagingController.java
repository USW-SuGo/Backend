package com.usw.sugo.domain.majorchatting.messaging.controller;


import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majorchatting.messaging.dto.ChattingMessage;
import com.usw.sugo.domain.majorchatting.messaging.service.ChattingRoomMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChattingRoomMessagingController {

    private final ChattingRoomMessagingService messagingService;
    private final SimpMessageSendingOperations sendingOperations;

    @GetMapping("/app")
    public String testHTML() {
        return "app";
    }

    /**
     MessageMapping : 클라이언트가 /chat/file 로 메시지를 보냈을 경우
     SendTo : 어떤 구독중인 Queue에 대한 Message인지
     */
    @MessageMapping("/message")
    @SendTo("/queue/message")
    public void message(ChattingMessage message) throws IOException {

        System.out.println("message = " + message.getMessage());
        System.out.println("message = " + message.getMultipartFileList().toString());

        // 채팅 내용이 메세지 일 때
        if (message.getType().equals("MESSAGE")) {
            // 요청한 채팅 메세지 DB 에 저장
            messagingService.uploadMessageByMessaging(message);

            // 메세지 구독자에게 발행
            sendingOperations.convertAndSend(
                    "/queue/" +
                    message.getChattingRoomId() +
                    message.getMessage());
        }
        // 채팅 내용이 파일일 때
        else if (message.getType().equals("FILE")) {
            // 요청한 채팅 파일 DB에 저장
            messagingService.uploadFileByMessaging(message);

            // 메세지 구독자에게 발행
            sendingOperations.convertAndSend(
                    "/queue/" +
                            message.getChattingRoomId() +
                            Arrays.toString(message.getMultipartFileList()));
        }
    }

//    /**
//     MessageMapping : 클라이언트가 /chat/file 로 메시지를 보냈을 경우
//     SendTo : 어떤 구독중인 Queue에 대한 Message인지
//     */
//    @MessageMapping("/file")
//    @SendTo("/queue/file")
//    public void file(@RequestBody ChattingMessage message) throws IOException {
//
//
//    }
}
