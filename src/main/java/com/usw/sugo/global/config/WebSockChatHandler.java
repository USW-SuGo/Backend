//package com.usw.sugo.global.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.usw.sugo.domain.majorchatting.ChattingRoom;
//import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChattingRoomMessageDto.ChatMessage;
//import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class WebSockChatHandler extends TextWebSocketHandler {
//    private final ObjectMapper objectMapper;
//    private final ChattingRoomRepository chattingRoomRepository;
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
//        ChattingRoom chattingRoom = chattingRoomRepository.findByRoomId(chatMessage.getRoomId()).get();
//        chattingRoom.handleActions(session, chatMessage, chattingRoomService);
//    }
//}
