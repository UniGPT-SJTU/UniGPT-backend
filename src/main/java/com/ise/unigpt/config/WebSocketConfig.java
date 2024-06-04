package com.ise.unigpt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.websocket.ChatWebSocketHandler;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.service.LLMServiceFactory;

import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;
    private final LLMServiceFactory llmServiceFactory;

    public WebSocketConfig(
        AuthService authService, 
        ChatHistoryService chatHistoryService,
        LLMServiceFactory llmServiceFactory
    ) {
        this.authService = authService;
        this.chatHistoryService = chatHistoryService;
        this.llmServiceFactory = llmServiceFactory;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(
            new ChatWebSocketHandler(
                authService, 
                chatHistoryService, 
                llmServiceFactory
            ), 
            "/chat"
        )
                .setAllowedOrigins("*");
    }
}