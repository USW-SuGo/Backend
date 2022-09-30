package com.usw.sugo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Topic : 채팅방
        // Pub : 메세지 전송
        // Sub : 채팅방 입장
        registry.setApplicationDestinationPrefixes("/publish");
        registry.enableSimpleBroker("/subscribe");

        // registry.enableSimpleBroker("/sub");
        // registry.setApplicationDestinationPrefixes("/pub");


        // 컨벤션 : "queue" : 1 vs 1 메세지
        // 컨벤션 : "topic" : 1 vs N 메세지
        // 내장 브로커 사용, 지정한 prefix 가 붙은 메세지를 브로커가 처리한다.
        // registry.enableSimpleBroker("/queue", "topic");
        //
        // 메세지 핸들러로 라우팅되는 prefix 이다.
        // registry.setApplicationDestinationPrefixes("/chatting");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 웹 소켓 연결주소 (HandShake를 위한 주소이다.)
        registry.addEndpoint("/ws-connetcion")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
