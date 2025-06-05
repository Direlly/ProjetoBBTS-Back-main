package com.example.config;

import com.example.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        logger.info("Registrando handler de WebSocket em /chat");
        registry.addHandler(this.chatWebSocketHandler, "/chat")
                .setAllowedOrigins("http://localhost:4200/chat");
    }
}