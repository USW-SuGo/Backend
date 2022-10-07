package com.usw.sugo.global.socket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/queue", "/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(socketHandler, "/chatting");
//    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//
//        // 웹 소켓 연결주소 (HandShake를 위한 주소이다.)
//        registry.addEndpoint("/stomp/chat")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//
//        // Topic : 채팅방
//        // Pub : 메세지 전송
//        // Sub : 채팅방 입장
//        registry.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
//        registry.setApplicationDestinationPrefixes("/publish");
//        registry.enableSimpleBroker("/subscribe");
//        registry.enableStompBrokerRelay("/queue", "topic", "exchange", "/amq/queue");
//
//        // registry.enableSimpleBroker("/sub");
//        // registry.setApplicationDestinationPrefixes("/pub");
//
//
//        // 컨벤션 : "queue" : 1 vs 1 메세지
//        // 컨벤션 : "topic" : 1 vs N 메세지
//        // 내장 브로커 사용, 지정한 prefix 가 붙은 메세지를 브로커가 처리한다.
//        // registry.enableSimpleBroker("/queue", "topic");
//        //
//        // 메세지 핸들러로 라우팅되는 prefix 이다.
//        // registry.setApplicationDestinationPrefixes("/chatting");
//    }
}
