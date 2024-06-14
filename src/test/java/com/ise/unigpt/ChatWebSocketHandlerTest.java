package com.ise.unigpt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.service.LLMServiceFactory;
import com.ise.unigpt.utils.TestHistoryFactory;
import com.ise.unigpt.websocket.ChatWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public class ChatWebSocketHandlerTest {

    @Mock
    private WebSocketSession session;

    @Mock
    private AuthService authService;

    @Mock
    private ChatHistoryService chatHistoryService;

    @InjectMocks
    private ChatWebSocketHandler chatWebSocketHandler;

    @Mock
    private LLMServiceFactory llmServiceFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        chatWebSocketHandler = spy(new ChatWebSocketHandler(authService, chatHistoryService, llmServiceFactory));
    }

    @Test
    public void testHandleTextMessageParseException() throws Exception {
        // 模拟消息内容
        TextMessage invalidMessage = new TextMessage("Invalid JSON format");

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, invalidMessage);

        // 验证是否发送了错误消息
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessage() throws Exception {
        // 模拟消息内容
        TextMessage message1 = new TextMessage("{\r\n" +
                "    \"historyId\": \"1043\"\r\n" +
                "}");
        TextMessage message2 = new TextMessage("{\"chatContent\": \"投喂可爱课程\", \"cover\": true, \"userAsk\": false}");

        // 创建模拟的 History 对象
        History history = mock(History.class);
        Bot bot = mock(Bot.class);
        User user = mock(User.class);
        when(history.getBot()).thenReturn(bot);
        when(history.getUser()).thenReturn(user);
        when(bot.getBaseModelAPI()).thenReturn(BaseModelType.GPT); // 假设 BaseModelType 有 GPT3
        when(user.getId()).thenReturn(1);

        // 模拟 chatHistoryService 和 authService 的行为
        when(chatHistoryService.getHistory(1043)).thenReturn(history);
        when(authService.getUserByToken(anyString())).thenReturn(user);

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 模拟 LLMServiceFactory 的行为
        LLMService llmService = mock(LLMService.class);
        when(llmServiceFactory.getLLMService(BaseModelType.GPT)).thenReturn(llmService);
        when(llmService.generateResponse(any(), any())).thenReturn("Response from LLMService");

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, message1);
        chatWebSocketHandler.handleTextMessage(session, message2);

        // 验证是否正确调用了相关方法
        verify(chatHistoryService, atLeast(1)).getHistory(1043);
        verify(authService, times(1)).getUserByToken(anyString());
        verify(history, times(1)).getUser();
        // 验证是否发送了消息
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageBot22() throws Exception {
        // 模拟消息内容
        TextMessage message1 = new TextMessage("{\r\n" +
                "    \"historyId\": \"1043\"\r\n" +
                "}");
        TextMessage message2 = new TextMessage("{\"chatContent\": \"投喂可爱课程\", \"cover\": true, \"userAsk\": false}");

        // 创建模拟的 History 对象
        History history = mock(History.class);
        Bot bot = mock(Bot.class);
        User user = mock(User.class);
        when(history.getBot()).thenReturn(bot);
        when(history.getUser()).thenReturn(user);
        when(bot.getBaseModelAPI()).thenReturn(BaseModelType.GPT); // 假设 BaseModelType 有 GPT3
        when(user.getId()).thenReturn(1);

        // 模拟 chatHistoryService 和 authService 的行为
        when(chatHistoryService.getHistory(1043)).thenReturn(history);
        when(authService.getUserByToken(anyString())).thenReturn(user);

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 模拟 LLMServiceFactory 的行为
        LLMService llmService = mock(LLMService.class);
        when(llmServiceFactory.getLLMService(BaseModelType.GPT)).thenReturn(llmService);
        when(llmService.generateResponse(any(), any())).thenReturn("Response from LLMService");

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, message1);
        chatWebSocketHandler.handleTextMessage(session, message2);

        // 验证是否正确调用了相关方法
        verify(chatHistoryService, atLeast(1)).getHistory(1043);
        verify(authService, times(1)).getUserByToken(anyString());
        verify(history, times(1)).getUser();
        // 验证是否发送了消息
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageNoResponse() throws Exception {
        // 模拟消息内容
        TextMessage message1 = new TextMessage("{\r\n" +
                "    \"historyId\": \"1043\"\r\n" +
                "}");
        TextMessage message2 = new TextMessage("{\"chatContent\": \"投喂可爱课程\", \"cover\": true, \"userAsk\": false}");

        // 创建模拟的 History 对象
        History history = mock(History.class);
        Bot bot = mock(Bot.class);
        User user = mock(User.class);
        when(history.getBot()).thenReturn(bot);
        when(history.getUser()).thenReturn(user);
        when(bot.getBaseModelAPI()).thenReturn(BaseModelType.GPT); // 假设 BaseModelType 有 GPT3
        when(user.getId()).thenReturn(1);

        // 模拟 chatHistoryService 和 authService 的行为
        when(chatHistoryService.getHistory(1043)).thenReturn(history);
        when(authService.getUserByToken(anyString())).thenReturn(user);

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, message1);
        chatWebSocketHandler.handleTextMessage(session, message2);

        // 验证是否正确调用了相关方法
        verify(chatHistoryService, atLeast(1)).getHistory(1043);
        verify(authService, times(1)).getUserByToken(anyString());
        verify(history, times(1)).getUser();
        // 验证是否发送了消息
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageHistory0() throws Exception {
        // 模拟消息内容
        TextMessage message1 = new TextMessage("{\r\n" +
                "    \"historyId\": \"0\"\r\n" +
                "}");
        TextMessage message2 = new TextMessage("{\r\n" +
                "    \"chatContent\": \"投喂可爱课程\"\r\n" +
                "    \"cover\": true\r\n" +
                "    \"userAsk\": true\r\n" +
                "}");

        // 创建模拟的 History 对象
        History history = mock(History.class);
        Bot bot = mock(Bot.class);
        User user = mock(User.class);
        when(history.getBot()).thenReturn(bot);
        when(history.getUser()).thenReturn(user);
        when(bot.getBaseModelAPI()).thenReturn(BaseModelType.GPT); // 假设 BaseModelType 有 GPT3
        when(user.getId()).thenReturn(1);

        // 模拟 chatHistoryService 和 authService 的行为
        when(chatHistoryService.getHistory(1043)).thenReturn(history);
        when(authService.getUserByToken(anyString())).thenReturn(user);

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, message1);
        chatWebSocketHandler.handleTextMessage(session, message2);

        verify(session, atMost(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessageUnauth() throws Exception {
        // 模拟消息内容
        TextMessage message1 = new TextMessage("{\r\n" +
                "    \"historyId\": \"1043\"\r\n" +
                "}");
        TextMessage message2 = new TextMessage("{\r\n" +
                "    \"chatContent\": \"投喂可爱课程\"\r\n" +
                "    \"cover\": true\r\n" +
                "    \"userAsk\": true\r\n" +
                "}");

        // 创建模拟的 History 对象
        History history = mock(History.class);
        Bot bot = mock(Bot.class);
        User user = mock(User.class);
        User user2 = mock(User.class);
        when(history.getBot()).thenReturn(bot);
        when(history.getUser()).thenReturn(user2);
        when(bot.getBaseModelAPI()).thenReturn(BaseModelType.GPT); // 假设 BaseModelType 有 GPT3
        when(user.getId()).thenReturn(2);

        // 模拟 chatHistoryService 和 authService 的行为
        when(chatHistoryService.getHistory(1043)).thenReturn(history);
        when(authService.getUserByToken(anyString())).thenReturn(user);

        // 模拟 WebSocketSession 的行为
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=abc123");
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // 调用 handleTextMessage 方法
        chatWebSocketHandler.afterConnectionEstablished(session);
        chatWebSocketHandler.handleTextMessage(session, message1);
        chatWebSocketHandler.handleTextMessage(session, message2);

        verify(session, atMost(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testHandleTextMessage_FirstMessage() throws Exception {
        TextMessage message = new TextMessage("Test message");

        doNothing().when(chatWebSocketHandler).handleFirstMessage(session, message.getPayload());
        doNothing().when(chatWebSocketHandler).handleSecondMessage(session, message.getPayload());

        chatWebSocketHandler.handleTextMessage(session, message);

        verify(chatWebSocketHandler, times(1)).handleFirstMessage(session, message.getPayload());
        verify(chatWebSocketHandler, times(0)).handleSecondMessage(session, message.getPayload());
    }

    @Test
    public void testHandleTextMessage_SecondMessage() throws Exception {
        // 从factory中获取LLMService
        LLMService llmService = mock(LLMService.class);
        when(llmServiceFactory.getLLMService(BaseModelType.fromValue(0))).thenReturn(llmService);
        TextMessage message = new TextMessage("{historyId: 1}");
        // 设置session对应的token

        doNothing().when(chatWebSocketHandler).handleFirstMessage(session, message.getPayload());
        doNothing().when(chatWebSocketHandler).handleSecondMessage(session, message.getPayload());

        // Simulate that the first message has been sent
        chatWebSocketHandler.handleTextMessage(session, message);
        chatWebSocketHandler.handleTextMessage(session, message);

        verify(chatWebSocketHandler, atLeastOnce()).handleFirstMessage(session, message.getPayload());
        verify(chatWebSocketHandler, atMost(3)).handleSecondMessage(session, message.getPayload());
        List<PromptChat> promptChatList = null;
        List<Chat> chatList = null;
        verify(llmService, atMostOnce()).generateResponse(promptChatList, chatList);
    }

    @Test
    public void testAfterConnectionEstablished() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=test_token");

        when(session.getHandshakeHeaders()).thenReturn(headers);

        chatWebSocketHandler.afterConnectionEstablished(session);
    }

    @Test
    public void testPreHandleNoCanvasUrl() throws Exception {
        User user = mock(User.class);
        when(authService.getUserByToken(anyString())).thenReturn(user);
        when(user.getCanvasUrl()).thenReturn(null);

        WebSocketSession session = mock(WebSocketSession.class);
        Map<WebSocketSession, String> sessionToken = new HashMap<>();
        sessionToken.put(session, "token");
        chatWebSocketHandler.sessionToken.putAll(sessionToken);

        chatWebSocketHandler.preHandle(session, 22, new ArrayList<>());

    }

    @Test
    public void testPreHandleInvalidCanvasUrl() throws Exception {
        User user = mock(User.class);
        when(authService.getUserByToken(anyString())).thenReturn(user);
        when(user.getCanvasUrl()).thenReturn("https://invalid.url");

        WebSocketSession session = mock(WebSocketSession.class);
        Map<WebSocketSession, String> sessionToken = new HashMap<>();
        sessionToken.put(session, "token");
        chatWebSocketHandler.sessionToken.putAll(sessionToken);

        chatWebSocketHandler.preHandle(session, 22, new ArrayList<>());

    }

    @Test
    public void testPreHandleCanvasEventError() throws Exception {
        User user = mock(User.class);
        when(authService.getUserByToken(anyString())).thenReturn(user);
        when(user.getCanvasUrl())
                .thenReturn("https://oc.sjtu.edu.cn/feeds/calendars/user_5ANNdRErwaHFWaUwCJuLqUk2kyoSNRwMGFtN933O.ics");

        WebSocketSession session = mock(WebSocketSession.class);
        Map<WebSocketSession, String> sessionToken = new HashMap<>();
        sessionToken.put(session, "token");
        chatWebSocketHandler.sessionToken.putAll(sessionToken);

        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn("invalid ics data");
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        chatWebSocketHandler.preHandle(session, 22, new ArrayList<>());

    }

    @Test
    public void testPreHandleCanvasEventSuccess() throws Exception {
        User user = mock(User.class);
        when(authService.getUserByToken(anyString())).thenReturn(user);
        when(user.getCanvasUrl()).thenReturn("https://oc.sjtu.edu.cn/feeds/calendars/user_test.ics");

        WebSocketSession session = mock(WebSocketSession.class);
        Map<WebSocketSession, String> sessionToken = new HashMap<>();
        sessionToken.put(session, "token");
        chatWebSocketHandler.sessionToken.putAll(sessionToken);

        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(
                "BEGIN:VCALENDAR\nBEGIN:VEVENT\nSUMMARY:Test Event\nDTSTART:20240101T000000Z\nEND:VEVENT\nEND:VCALENDAR");
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        List<PromptChat> promptChatList = new ArrayList<>();
        chatWebSocketHandler.preHandle(session, 22, promptChatList);

        verify(session, never()).sendMessage(any(TextMessage.class));
        assert promptChatList.size() == 1;
        assert promptChatList.get(0).getContent().contains("Here are my upcoming Canvas events");
    }
}