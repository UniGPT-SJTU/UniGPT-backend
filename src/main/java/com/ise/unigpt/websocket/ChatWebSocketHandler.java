package com.ise.unigpt.websocket;

import biweekly.Biweekly;
import biweekly.ICalendar;
import dev.langchain4j.service.TokenStream;

import com.ise.unigpt.dto.CanvasEventDTO;
import com.ise.unigpt.dto.WebSocketClientMsgDTO;
import com.ise.unigpt.dto.WebSocketConnMsgDTO;
import com.ise.unigpt.dto.WebSocketServerMsgDTO;
import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.ChatType;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ise.unigpt.model.History;
import com.ise.unigpt.model.User;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.service.LLMService.GenerateResponseOptions;
import com.ise.unigpt.utils.JsonUtils;

import com.ise.unigpt.service.LLMServiceFactory;

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

    private final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

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
        log.info("ConnectionEstablished. Headers: " + headers);
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
            log.info("token is " + token);
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
            handleFirstMessage(session, payLoad);
        } else {
            handleSecondMessage(session, payLoad);
        }

    }

    public void handleFirstMessage(WebSocketSession session, String payLoad) {
        try {
            log.info("Received first message: " + payLoad);

            // 获得historyId
            Integer historyId = JsonUtils.fromJson(payLoad, WebSocketConnMsgDTO.class).getHistoryId();
            if (historyId == 0) {
                throw new RuntimeException("Invalid history id");
            }

            History history = chatHistoryService.getHistory(historyId);
            sessionHistory.put(session, history);

            // 检查用户是否有权限访问history
            User user = authService.getUserByToken(sessionToken.get(session));
            Integer userId = user.getId();
            Integer historyUserId = chatHistoryService.getHistory(historyId).getUser().getId();

            if (!userId.equals(historyUserId)) {
                throw new RuntimeException("You are not authorized to access this history");
            }
            log.info("User authorized to access history");

            // 设置session的firstMessageSent为true
            sessionFirstMessageSent.put(session, true);

            // 设置 LLMServiceImpl
            BaseModelType baseModelType = history.getLlmArgs().getBaseModelType();
            sessionBaseModelType.put(session, baseModelType);
        } catch (Exception e) {
            handleRuntimeException(session, e);
        }
    }

    public void handleSecondMessage(WebSocketSession session, String payLoad) {
        // 这是第二种消息
        try {
            log.info("Received second message: " + payLoad);
            // 发送回复消息

            History history = sessionHistory.get(session);

            // 获取用户的消息
            WebSocketClientMsgDTO clientMsgDTO = JsonUtils.fromJson(payLoad, WebSocketClientMsgDTO.class);

            // 获取用户的消息
            String userMessage = clientMsgDTO.getChatContent();

            // 更新历史的最近活跃时间
            chatHistoryService.updateHistoryActiveTime(history);

            Boolean cover = clientMsgDTO.getCover();
            Boolean isUserAsk = clientMsgDTO.getUserAsk();

            // 如果cover为true，则删除末尾的两个对话
            if (cover) {
                chatHistoryService.deleteLastRoundOfChats(history.getId(), sessionToken.get(session));
            }
            // 生成回复消息
            TokenStream tokenStream = llmServiceFactory
                    .getLLMService(sessionBaseModelType.get(session))
                    .generateResponse(
                            history,
                            userMessage,
                            GenerateResponseOptions.builder()
                                    .cover(cover)
                                    .isUserAsk(isUserAsk)
                                    .sendFunctionCall((thisSession, replyMessage) -> {
                                        sendMessageWrapper(session,
                                                JsonUtils.toJson(new WebSocketServerMsgDTO("toolExecutionRequest", replyMessage)));
                                    })
                                    .sendFunctionResult((thisSession, replyMessage) -> {
                                        sendMessageWrapper(session,
                                                JsonUtils.toJson(new WebSocketServerMsgDTO("toolExecutionResult", replyMessage)));
                                    })
                                    .build());
            AtomicReference<String> replyMessageRef = new AtomicReference<>();
            AtomicReference<History> historyRef = new AtomicReference<>(history);
            tokenStream.onNext(token -> {
                log.info("Response stream on next");
                sendMessageWrapper(session, JsonUtils.toJson(
                        new WebSocketServerMsgDTO("token", token)));
            }).onComplete(response -> {
                // 发送报文：
                log.info("Response stream on complete");
                replyMessageRef.set(response.content().text());
                try {
                    sendMessageWrapper(session,
                            JsonUtils.toJson(new WebSocketServerMsgDTO("complete", replyMessageRef.get())));
                    // 将用户的消息存入history
                    if (!isUserAsk) {
                        chatHistoryService.createChat(history.getId(), userMessage, ChatType.USER,
                                sessionToken.get(session));
                    }

                    // 将回复内容存入history
                    chatHistoryService.createChat(historyRef.get().getId(), replyMessageRef.get(), ChatType.BOT,
                            sessionToken.get(session));
                } catch (Exception e) {
                    log.error("on complete error");
                    log.error(e.getMessage());
                }
            }).onError(error -> {
                handleRuntimeException(session, error);
            }).start();

        } catch (Exception e) {
            handleRuntimeException(session, e);
        }
    }

    private void sendMessageWrapper(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRuntimeException(WebSocketSession session, Throwable e) {
        log.error(e.getMessage());
        e.printStackTrace();
        sendMessageWrapper(session, JsonUtils.toJson(new WebSocketServerMsgDTO("error", e.getMessage())));
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
                return "我的canvas链接是错误的，请回答我“很抱歉，由于您在个人主页添加的Canvas链接是错误的，我无法帮助您规划任务。"
                        + "在您修改Canvas链接后，可以再次与我对话，我将很乐意帮助您规划任务安排。祝您顺利完成所有任务！";
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
