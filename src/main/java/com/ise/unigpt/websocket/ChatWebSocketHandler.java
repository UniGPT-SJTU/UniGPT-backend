package com.ise.unigpt.websocket;

import biweekly.Biweekly;
import biweekly.ICalendar;
import com.ise.unigpt.dto.CanvasEventDTO;

import com.ise.unigpt.model.BaseModelType;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import io.micrometer.common.lang.NonNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.service.LLMServiceFactory;
import com.ise.unigpt.service.LLMService.GenerateResponseOptions;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebSocketMessageBroker
@CrossOrigin(origins = "http://localhost:3000")
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    private final Map<WebSocketSession, Boolean> sessionFirstMessageSent;
    public final Map<WebSocketSession, String> sessionToken;
    private final Map<WebSocketSession, History> sessionHistory;
    private final Map<WebSocketSession, BaseModelType> sessionBaseModelType;

    private final LLMServiceFactory llmServiceFactory;

    public ChatWebSocketHandler(
            AuthService authService,
            ChatHistoryService chatHistoryService,
            LLMServiceFactory llmServiceFactory) {
        this.authService = authService;
        this.chatHistoryService = chatHistoryService;

        this.sessionFirstMessageSent = new HashMap<>();
        this.sessionHistory = new HashMap<>();
        this.sessionToken = new HashMap<>();
        this.sessionBaseModelType = new HashMap<>();

        this.llmServiceFactory = llmServiceFactory;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 获取握手阶段的HTTP头
        Map<String, List<String>> headers = session.getHandshakeHeaders();

        System.out.println("Headers: ");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        // 获取Cookie头
        List<String> cookies = headers.get("Cookie");

        // 解析Cookie头以获取token的值
        String token = null;
        if (cookies != null) {
            token = cookies.stream()
                    .flatMap(cookie -> Arrays.stream(cookie.split(";")))
                    .map(String::trim)
                    .filter(part -> part.startsWith("token="))
                    .map(part -> part.substring("token=".length()))
                    .findFirst()
                    .orElse(null);
        }

        if (token != null) {
            System.out.println("Token: " + token);
            sessionToken.put(session, token);
        }
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
        String errorMessage = "Error parsing historyId";

        try {
            System.out.println("Received first message: " + payLoad);

            // 获得historyId
            int historyId;
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(payLoad, Map.class);
            String historyIdString = map.get("historyId");
            historyId = Integer.parseInt(historyIdString);
            if (historyId == 0) {
                errorMessage = "Please provide a valid history id";
                throw new Exception();
            }

            History history = chatHistoryService.getHistory(historyId);
            System.out.println("History: " + history.getId());
            sessionHistory.put(session, history);
            System.out.println("Save history: " + sessionHistory.get(session).getId());

            // 检查用户是否有权限访问history
            User user = authService.getUserByToken(sessionToken.get(session));
            Integer userId = user.getId();
            System.out.println("User: " + userId);
            Integer historyUserId = chatHistoryService.getHistory(historyId).getUser().getId();
            System.out.println("History user: " + historyUserId);

            if (!userId.equals(historyUserId)) {
                String replyMessage = "You are not authorized to access this history";
                Map<String, String> replyMap = new HashMap<>();
                replyMap.put("replyMessage", replyMessage);
                errorMessage = new ObjectMapper().writeValueAsString(replyMap);
                throw new Exception();
            }

            // 设置session的firstMessageSent为true
            sessionFirstMessageSent.put(session, true);

            // 设置 LLMServiceImpl
            BaseModelType baseModelType = history.getBot().getBaseModelAPI();
            sessionBaseModelType.put(session, baseModelType);
        } catch (Exception e) {
            try {
                session.sendMessage(new TextMessage(errorMessage));
            } catch (Exception e2) {
                System.out.println("Error sending error message");
            }
        }

    }

    public void handleSecondMessage(WebSocketSession session, String payLoad) {
        // 这是第二种消息
        try {
            // 发送回复消息
            System.out.println("Received second message: " + payLoad);

            History history = sessionHistory.get(session);

            // preHandle(session, bot.getId(), promptChatList);

            // 获取用户的消息
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("objectMapper: " + objectMapper);
            Map<String, Object> map = objectMapper.readValue(payLoad, Map.class);

            // 获取用户的消息
            String userMessage = (String) map.get("chatContent");

            // double temperature = bot.getTemperature();
            // // TODO: 温度的特殊情况不应该写在这里
            // if (bot.getBaseModelAPI() == BaseModelType.GPT) {
            // temperature = temperature * 2;
            // }

            // 更新历史的最近活跃时间
            chatHistoryService.updateHistoryActiveTime(history);

            // 生成回复消息
            Boolean cover = (Boolean) map.get("cover");
            Boolean isUserAsk = (Boolean) map.get("userAsk");
            String replyMessage = llmServiceFactory
                    .getLLMService(sessionBaseModelType.get(session))
                    .generateResponse(
                            history,
                            userMessage,
                            GenerateResponseOptions.builder()
                                    .cover(cover)
                                    .isUserAsk(isUserAsk)
                                    .build());

                                    
            Map<String, String> replyMap = new HashMap<>();
            replyMap.put("replyMessage", replyMessage);
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(replyMap)));
            // 如果cover为true，则删除末尾的两个对话
            if (cover) {
                chatHistoryService.deleteChats(history.getId(), 2, sessionToken.get(session));
            }
            // 将用户的消息存入history
            if (!isUserAsk) {
                chatHistoryService.createChat(history.getId(), userMessage, ChatType.USER, sessionToken.get(session));
            }

            // 将回复内容存入history
            chatHistoryService.createChat(history.getId(), replyMessage, ChatType.BOT, sessionToken.get(session));

        } catch (Exception e) {
            System.out.println("Error sending second reply message");
            try {
                System.out.println(e.getMessage());
                String replyMessage = "Error sending second reply message";
                Map<String, String> replyMap = new HashMap<>();
                replyMap.put("replyMessage", replyMessage);
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(replyMap)));
            } catch (Exception e2) {
                System.out.println("Error sending error message");
            }
        }
    }

    public String getCanvasEventList(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String icsData = response.body();
            if (Biweekly.parse(icsData).first() == null) {
                return "我的canvas链接是错误的，请回答我“很抱歉，由于您在个人主页添加的Canvas链接是错误的，我无法帮助您规划任务。" +
                        "在您修改Canvas链接后，可以再次与我对话，我将很乐意帮助您规划任务安排。祝您顺利完成所有任务！";
            }
            ICalendar ical = Biweekly.parse(icsData).first();

            LocalDateTime now = LocalDateTime.now();

            return ical.getEvents().stream()
                    .filter(event -> event.getDateStart() != null)
                    .filter(event -> {
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(),
                                ZoneOffset.UTC);
                        return endDate.isAfter(now);
                    })
                    .map(event -> {
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(),
                                ZoneOffset.UTC);
                        endDate = endDate.plusHours(8);
                        if (endDate.getHour() == 0) {
                            endDate = endDate.plusDays(1);
                        }
                        Instant ddlTime = endDate.toInstant(ZoneOffset.UTC);
                        if (event.getDescription() == null) {
                            return new CanvasEventDTO(event.getSummary().getValue(), "No description", ddlTime);
                        }
                        return new CanvasEventDTO(
                                event.getSummary().getValue(),
                                event.getDescription().getValue(),
                                ddlTime);
                    })
                    .toList().toString();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "error";
        } catch (Exception e) {
            return "error";
        }
    }
}