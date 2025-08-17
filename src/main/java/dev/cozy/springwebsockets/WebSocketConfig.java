package dev.cozy.springwebsockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    // Register the WebSocket handler mapping
    @Bean
    public SimpleUrlHandlerMapping webSocketHandlerMapping(ChatWebSocketHandler chatHandler) {
        return new SimpleUrlHandlerMapping(Map.of(
                "/ws/chat", chatHandler // Map path to handler
        ), 1); // order
    }

    // Required bean to enable WebSocket support in WebFlux
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}