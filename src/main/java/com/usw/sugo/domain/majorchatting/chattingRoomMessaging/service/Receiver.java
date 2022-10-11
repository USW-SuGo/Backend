package com.usw.sugo.domain.majorchatting.chattingRoomMessaging.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.dto.ChattingMessage;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private final SimpMessagingTemplate template;
    private final UserRepository userRepository;

    @KafkaListener(id = "main-listener", topics = "kafka-chatting")
    public void receive(ChattingMessage message) throws Exception{
        LOGGER.info("message='{}'", message);
        HashMap<String, String> msg = new HashMap<>();
        msg.put("message", message.getMessage());
        msg.put("author", String.valueOf(message.getSenderId()));

        ObjectMapper mapper = new ObjectMapper(); // 왜 쓰는거지?
        String json = mapper.writeValueAsString(msg);

        this.template.convertAndSend("/topic/public", json);
    }
}
