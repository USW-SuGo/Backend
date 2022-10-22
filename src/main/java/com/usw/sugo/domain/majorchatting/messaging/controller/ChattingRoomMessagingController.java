package com.usw.sugo.domain.majorchatting.messaging.controller;


import com.usw.sugo.domain.majorchatting.messaging.dto.ChattingMessage;
import com.usw.sugo.domain.majorchatting.messaging.service.ChattingRoomMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChattingRoomMessagingController {

    private final ChattingRoomMessagingService messagingService;

    private final RabbitTemplate rabbitTemplate;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    /**
     * 채팅방 개설
     * @param message
     * @param chattingRoomId
     */
    @MessageMapping("chat.message.{chattingRoomId}")
    public void enter(ChattingMessage message, @DestinationVariable Long chattingRoomId) {

        message.setMessage("채팅방이 개설되었습니다.");
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chattingRoomId, message);
    }

    /**
     * 채팅방 메세지 전송
     * 텍스트 메세지인지, 파일인지 구분 후 각 유형에 따른 DB 저장 로직 분리
     * @param message
     * @param chattingRoomId
     */
    @MessageMapping("chat.message.{chattingRoomId}")
    public void send(ChattingMessage message, @DestinationVariable Long chattingRoomId) throws IOException {
        // 채팅 내용이 메세지 일 때
        if (message.getType().equals("MESSAGE")) {
            // 요청한 채팅 메세지 DB 에 저장
            messagingService.uploadMessageByMessaging(message, chattingRoomId);
        }
        // 채팅 내용이 파일일 때
        else if (message.getType().equals("FILE")) {
            // 요청한 채팅 파일 DB에 저장
            messagingService.uploadFileByMessaging(message, chattingRoomId);
        }
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chattingRoomId, message);
    }
}
