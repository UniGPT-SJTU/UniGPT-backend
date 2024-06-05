package com.ise.unigpt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.LLMServiceImpl;
import com.ise.unigpt.utils.TestPromptChatFactory;
import com.mashape.unirest.http.Unirest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class LLMServiceTest {

    private LLMService llmService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testOpenAIGenerateResponse() throws Exception {
        List<PromptChat> promptChats = new ArrayList<>();
        llmService = new LLMServiceImpl(BaseModelType.fromValue(0));
        promptChats.add(TestPromptChatFactory.createUserPromptChat());
        promptChats.add(TestPromptChatFactory.createBotPromptChat());
        List<Chat> chats = new ArrayList<>();
        ChatType userChatType = ChatType.USER;
        ChatType botChatType = ChatType.BOT;
        chats.add(new Chat(userChatType, "Hello"));
        chats.add(new Chat(botChatType, "Hi"));
        chats.add(new Chat(userChatType, "How are you?"));

        String result = llmService.generateResponse(promptChats, chats);

        System.out.println(result);
        // 判断result是否为空
        assert result != null;
        // 判断result是否为字符串
        assert result instanceof String;
    }

    @Test
    public void testClaudeGenerateResponse() throws Exception {
        List<PromptChat> promptChats = new ArrayList<>();
        llmService = new LLMServiceImpl(BaseModelType.fromValue(1));
        promptChats.add(TestPromptChatFactory.createUserPromptChat());
        promptChats.add(TestPromptChatFactory.createBotPromptChat());
        List<Chat> chats = new ArrayList<>();
        ChatType userChatType = ChatType.USER;
        ChatType botChatType = ChatType.BOT;
        chats.add(new Chat(userChatType, "Hello"));
        chats.add(new Chat(botChatType, "Hi"));
        chats.add(new Chat(userChatType, "How are you?"));

        String result = llmService.generateResponse(promptChats, chats);

        System.out.println(result);
        // 判断result是否为空
        assert result != null;
        // 判断result是否为字符串
        assert result instanceof String;
    }

    @Test
    public void testLlamaGenerateResponse() throws Exception {
        List<PromptChat> promptChats = new ArrayList<>();
        llmService = new LLMServiceImpl(BaseModelType.fromValue(2));
        promptChats.add(TestPromptChatFactory.createUserPromptChat());
        promptChats.add(TestPromptChatFactory.createBotPromptChat());
        List<Chat> chats = new ArrayList<>();
        ChatType userChatType = ChatType.USER;
        ChatType botChatType = ChatType.BOT;
        chats.add(new Chat(userChatType, "Hello"));
        chats.add(new Chat(botChatType, "Hi"));
        chats.add(new Chat(userChatType, "How are you?"));

        String result = llmService.generateResponse(promptChats, chats);

        System.out.println(result);
        // 判断result是否为空
        assert result != null;
        // 判断result是否为字符串
        assert result instanceof String;
    }

    @Test
    public void testKimiGenerateResponse() throws Exception {
        List<PromptChat> promptChats = new ArrayList<>();
        llmService = new LLMServiceImpl(BaseModelType.fromValue(3));
        promptChats.add(TestPromptChatFactory.createUserPromptChat());
        promptChats.add(TestPromptChatFactory.createBotPromptChat());
        List<Chat> chats = new ArrayList<>();
        ChatType userChatType = ChatType.USER;
        ChatType botChatType = ChatType.BOT;
        chats.add(new Chat(userChatType, "Hello"));
        chats.add(new Chat(botChatType, "Hi"));
        chats.add(new Chat(userChatType, "How are you?"));

        String result = llmService.generateResponse(promptChats, chats);

        System.out.println(result);
        // 判断result是否为空
        assert result != null;
        // 判断result是否为字符串
        assert result instanceof String;
    }

    private void verifyStatic(Class<Unirest> class1, VerificationMode times) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyStatic'");
    }
}