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
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final LLMService llmService;
    private final AuthService authService;
    private final Map<WebSocketSession, Integer> sessionData;
    private final Map<WebSocketSession, Boolean> sessionFirstMessageSent;
    private final Map<WebSocketSession, String> sessionToken;

    public ChatWebSocketHandler(AuthService authService) {
        this.llmService = new OpenAIService();
        this.sessionFirstMessageSent = new HashMap<>();
        this.sessionData = new HashMap<>();
        this.sessionToken = new HashMap<>();
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
            System.out.println("Received first message: " + payLoad);
            ObjectMapper objectMapper = new ObjectMapper();
            Integer historyId = 0;
            try {
                Map<String, String> map = objectMapper.readValue(payLoad, Map.class);
                String historyIdString = map.get("historyId");
                historyId = Integer.parseInt(historyIdString);
                sessionFirstMessageSent.put(session, true);
                sessionData.put(session, historyId);
                User = authService.getUserByToken(sessionToken.get(session));
                try {
                    // 发送回复消息
                    String replyMessage = "Hello, I am a chatbot. How can I help you?";
                    // 将replayMessage转化为json格式
                    Map<String, String> replyMap = new HashMap<>();
                    replyMap.put("message", replyMessage);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(replyMap)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                String errorMessage = "Error parsing historyId";
                try {
                    session.sendMessage(new TextMessage(errorMessage));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

        } else {
            // 这是第二种消息
            // TODO: 处理第二种消息
            try {
                // 发送回复消息
                System.out.println("Received second message: " + payLoad);
                session.sendMessage(new TextMessage("Your history id is: " + sessionData.get(session)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    String testChat() {
        List<PromptChat> promptChatList = new ArrayList<>();
        // 机器人创建时的模板对话
        promptChatList.add(new PromptChat(PromptChatType.SYSTEM, "你是一个++{lang}大师，精通++{lang}的所有语法和库。"));
        promptChatList.add(new PromptChat(PromptChatType.USER, "如何使用++{lang}++{question}？"));

        // 历史在创建时，用户填入的提示词键值对
        Map<String, String> promptList = Map.of(
                "lang", "java",
                "question", "计算1-100的和");

        // 用户创建历史之后，和机器人的对话
        List<Chat> chatList = new ArrayList<>();
        // 机器人回答
        chatList.add(new Chat(ChatType.BOT,
                "你可以使用一个简单的for循环来计算1到100这一范围的所有数字的和。以下是一个示例代码： ```java public class CalculateSum { public static void main(String[] args) { int sum = 0; for (int i = 1; i <= 100; i++) { sum += i; } System.out.println(\"1到100的和为：\" + sum); } } ``` 在这个示例中，我们初始化一个变量`sum`为0，然后使用for循环从1累加到100，最后打印出1到100的和。你可以运行这段代码来得到结果。"));
        // 用户追问
        chatList.add(new Chat(ChatType.USER, "有没有更加简洁的求和方式？"));
        try {
            return llmService.generateResponse(promptChatList, promptList, chatList);
        } catch (Exception e) {
            return "error met";
        }
    }
}