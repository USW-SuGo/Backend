package com.usw.sugo.global.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 22/10/14 문제 해결 이 구문을 추가 안하니까 Postman 에서 소켓 연결이 되지 않았음.
        registry
                .addEndpoint("/connect")
                .setAllowedOrigins("*");
        registry.addEndpoint("/connect")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    // /queue 경로로 접근 시 구독 요청을 할 때 쓰인다.
    // /chat 경로로 접근 시 메세지를 보낼 때 쓰인다.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        config.setPathMatcher(new AntPathMatcher("."));
        config.enableStompBrokerRelay(
                "/queue", "/topic", "/exchange", "/amq/queue");
    }
}