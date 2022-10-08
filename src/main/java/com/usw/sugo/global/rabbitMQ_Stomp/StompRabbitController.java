package com.usw.sugo.global.rabbitMQ_Stomp;

import com.usw.sugo.domain.majorchatting.chattingRoom.dto.FileRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.MessageRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompRabbitController {

    private final RabbitTemplate template;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";
    private final static String TEST_CHAT_QUEUE_NAME = "sample.queue";

    private final ChattingRoomService chattingRoomService;

    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(MessageRequest messageRequest, @DestinationVariable String chatRoomId) {
        messageRequest.setMessage("채팅방 입장 테스트");

        // exchange
        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, messageRequest);
    }


    @MessageMapping("chat.message.{chatRoomId}")
    public void sendMessage(MessageRequest messageRequest, @DestinationVariable String chatRoomId) {

        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, messageRequest);
        chattingRoomService.saveMessages(messageRequest);
    }

    @MessageMapping("chat.file.{chatRoomId}")
    public void sendFile(FileRequest fileRequest, @DestinationVariable String chatRoomId) throws IOException {

        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, fileRequest);
        chattingRoomService.saveFiles(fileRequest);
    }

    // receiver()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그 용도)
    @RabbitListener(queues = TEST_CHAT_QUEUE_NAME)
    public void receive(MessageRequest messageRequest) {
        System.out.println(("chatDto.getMessage = {}" + messageRequest));
    }
}
