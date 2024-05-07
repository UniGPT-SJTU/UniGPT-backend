package com.ise.unigpt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;
import com.ise.unigpt.websocket.ChatWebSocketHandler;
import com.ise.unigpt.service.ChatHistoryService;

import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    public WebSocketConfig(AuthService authService, ChatHistoryService chatHistoryService) {
        this.authService = authService;
        this.chatHistoryService = chatHistoryService;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(authService, chatHistoryService), "/chat");
    }
}
