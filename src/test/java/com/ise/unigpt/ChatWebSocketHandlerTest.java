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
import com.ise.unigpt.websocket.ChatWebSocketHandler;
import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;

import static org.mockito.Mockito.*;

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
    public void testHandleTextMessage() throws Exception {
        TextMessage message = new TextMessage("Test message");

        chatWebSocketHandler.handleTextMessage(session, message);

        // Add your assertions here to verify the behavior of handleTextMessage
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
        TextMessage message = new TextMessage("Test message");

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

        verify(chatWebSocketHandler, times(1)).sessionToken.put(session, "test_token");
    }
}