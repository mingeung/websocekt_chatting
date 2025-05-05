package com.example.websocket_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * TODO : 메시지 브로커 구성.
     * ! 메시지를 클라이언트에게 브로드캐스트할 때 사용할 주제 정의.
     * ! 클라이언트에서 서버로 메시지를 보낼 때 사용할 애플리케이션 목적지 접두사 설정
     * * -> 라우팅 규칙 정의하는 것
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ! sub으로 시작하는 목적지로 메시지 전달(해당 주소를 구독하는 클라이언트에게 메시지 보냄)
        config.enableSimpleBroker("/sub", "/user");
        // ! 클라이언트가 서버로 메시지 보낼 때 사용할 접두사 지정 -> 클라이언트가 "/pub"으로 시작하는 목적지로 메시지를 보내면 @MessageMapping이 달린 메서드로 라우팅
        config.setApplicationDestinationPrefixes("/pub");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * TODO : STOMP 엔드포인트 등록
     * ! 클라이언트가 WebSocket 연결을 맺기 위해 사용할 엔드포인트 URL 지정.
     * ! /stomp/chat으로 클라이언트가 WebSocjet 연결을 맺기 위해 접속할 URL 경로
     * ? withSockJS()는 WebSocket을 지원하지 않는 브라우저에서도 동작하도록. 폴백 옵션 제공
     * * -> 이 메서드는 클라이언트가 서버와 WebSocket 연결을 시작하는 진입점 역할
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOriginPatterns("*");
    }

@Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(8192)
                .setSendBufferSizeLimit(8192)
                .setSendTimeLimit(10000);
    }


}

