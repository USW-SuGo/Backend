package com.usw.sugo.global.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChatRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.FileRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majorchatting.chattingRoom.service.ChattingRoomService;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.Valid;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChattingRoomService chattingRoomService;
    private final ChattingRoomRepository chattingRoomRepository;

    // 메세지 전송
    @MessageMapping("/messages")
    public void chat(@Valid ChatRequest chatRequest) {
        chattingRoomService.saveMessages(chatRequest);
    }

    // 이미지 전송
    @MessageMapping("/files")
    public void postFile(@Valid FileRequest fileRequest) throws IOException {
        chattingRoomService.saveFiles(fileRequest);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        log.info("payload {}", payload);

        ChatRequest chatRequest = objectMapper.readValue(payload, ChatRequest.class);

        // 채팅방이 존재하는지 확인
        chattingRoomRepository.findById(chatRequest.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

        chattingRoomService.saveMessages(chatRequest);

    }
}

