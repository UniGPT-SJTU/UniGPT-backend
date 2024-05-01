package com.ise.unigpt.websocket;

import com.ise.unigpt.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatWebSocketHandler extends TextWebSocketHandler {

//    private final LLMService llmService;

//    ChatWebSocketHandler(LLMService llmService) {
//        this.llmService = llmService;
//    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后的处理
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理用户发送的消息
        String userMessage = message.getPayload();

        // 使用 OpenAI API 获取 ChatGPT 的回复
        String chatGptReply = callOpenAIAPI(userMessage);

        // 将 ChatGPT 的回复发送给客户端
        session.sendMessage(new TextMessage(chatGptReply));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭后的处理
    }


    private String callOpenAIAPI(String userMessage) {
        // 调用 OpenAI API 获取 ChatGPT 的回复
        // 此处实现你的 API 调用逻辑
        return "ChatGPT 回复";
    }
}
