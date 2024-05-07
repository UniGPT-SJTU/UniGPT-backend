package com.ise.unigpt.websocket;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.model.User;
import com.ise.unigpt.model.Bot;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final LLMService llmService;
    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    private final Map<WebSocketSession, Boolean> sessionFirstMessageSent;
    private final Map<WebSocketSession, String> sessionToken;
    private final Map<WebSocketSession, History> sessionHistory;

    public ChatWebSocketHandler(AuthService authService, ChatHistoryService chatHistoryService) {
        this.llmService = new OpenAIService();
        this.sessionFirstMessageSent = new HashMap<>();
        this.sessionHistory = new HashMap<>();
        this.sessionToken = new HashMap<>();
        this.chatHistoryService = chatHistoryService;
        this.authService = authService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取握手阶段的HTTP头
        Map<String, List<String>> headers = session.getHandshakeHeaders();

        // 获取Cookie头
        List<String> cookies = headers.get("Cookie");

        // 解析Cookie头以获取token的值
        String token = null;
        if (cookies != null) {
            for (String cookie : cookies) {
                String[] parts = cookie.split(";");
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("token=")) {
                        token = part.substring("token=".length());
                        break;
                    }
                }
                if (token != null) {
                    break;
                }
            }
        }

        if (token != null) {
            System.out.println("Token: " + token);
            sessionToken.put(session, token);
        } else {
            System.out.println("No token found");
        }

        // ...
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        // 获取消息的payload（body）
        String payLoad = message.getPayload();

        // 检查是否已经发送过第一种消息
        Boolean firstMessageSent = sessionFirstMessageSent.get(session);
        if (firstMessageSent == null || !firstMessageSent) {
            System.out.println("handleFirstMessage");
            handleFirstMessage(session, payLoad);
            System.out.println("handleFirstMessage done");
        } else {
            System.out.println("handleSecondMessage");
            handleSecondMessage(session, payLoad);
            System.out.println("handleSecondMessage done");
        }

    }

    public void handleFirstMessage(WebSocketSession session, String payLoad) {
        System.out.println("Received first message: " + payLoad);

        // 获得historyId
        Integer historyId = 0;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(payLoad, Map.class);
            String historyIdString = map.get("historyId");
            historyId = Integer.parseInt(historyIdString);
        } catch (Exception e) {
            String errorMessage = "Error parsing historyId";
            try {
                session.sendMessage(new TextMessage(errorMessage));
                return;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        // 保存session的history
        if (historyId == 0) {
            try {
                session.sendMessage(new TextMessage("Please provide a valid history id"));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        History history = chatHistoryService.getHistory(historyId);
        System.out.println("History: " + history.getId());
        sessionHistory.put(session, history);
        System.out.println("Save history: " + sessionHistory.get(session).getId());

        // 检查用户是否有权限访问history
        User user = authService.getUserByToken(sessionToken.get(session));
        Integer userId = user.getId();
        Integer historyUserId = chatHistoryService.getHistory(historyId).getUser().getId();
        if (!userId.equals(historyUserId)) {
            try {
                session.sendMessage(new TextMessage("You are not authorized to access this history"));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // 发送回复消息
        try {
            String replyMessage = "Hello, I am a chatbot. How can I help you?";
            Map<String, String> replyMap = new HashMap<>();
            replyMap.put("message", replyMessage);
            ObjectMapper objectMapper = new ObjectMapper();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(replyMap)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置session的firstMessageSent为true
        sessionFirstMessageSent.put(session, true);
    }

    public void handleSecondMessage(WebSocketSession session, String payLoad) {
        // 这是第二种消息
        try {
            // 发送回复消息
            System.out.println("Received second message: " + payLoad);

            History history = sessionHistory.get(session);

            Bot bot = history.getBot();
            System.out.println("Bot: " + bot.getId());
            List<PromptChat> promptChatList = bot.getPromptChats();
            System.out.println("PromptChatList: ");
            if (promptChatList == null) {
                System.out.println("PromptChatList is null");
                promptChatList = new ArrayList<>();
            }
            for (PromptChat promptChat : promptChatList) {
                System.out.println(promptChat.getContent());
            }

            Map<String, String> promptList = history.getPromptList();
            if (promptList == null) {
                System.out.println("PromptList is null");
                promptList = new HashMap<>();
            }
            System.out.println("PromptList: ");
            for (Map.Entry<String, String> entry : promptList.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            List<Chat> chatList = history.getChats();
            System.out.println("ChatList: ");
            for (Chat chat : chatList) {
                System.out.println(chat.getContent());
            }

            String replyMessage = llmService.generateResponse(promptChatList, promptList, chatList);
            Map<String, String> replyMap = new HashMap<>();
            replyMap.put("replyMessage", replyMessage);
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(replyMap)));
            // 将恢复内容存入history
            chatHistoryService.createChat(history.getId(), replyMessage, ChatType.BOT, sessionToken.get(session));
        } catch (Exception e) {
            System.out.println("Error sending second reply message");
            try {
                System.out.println(e.getMessage());
                session.sendMessage(new TextMessage("Error sending second reply message"));
            } catch (Exception e2) {
                System.out.println("Error sending error message");
            }
            e.printStackTrace();
        }
    }
}