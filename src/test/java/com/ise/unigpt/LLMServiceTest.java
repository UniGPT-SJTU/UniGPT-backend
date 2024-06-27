package com.ise.unigpt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.LLMServiceImpl;
import com.ise.unigpt.utils.TestPromptChatFactory;
import com.mashape.unirest.http.Unirest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


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
        double temperature = 0.5;

        String result = llmService.generateResponse(promptChats, chats, temperature);

        System.out.println(result);
        // 判断result是否为空
        assert result != null;
        // 判断result是否为字符串
        assert result instanceof String;
    }

   @Test
    public void testOpenAIGenerateResponseConcurrently() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个固定大小的线程池
        List<Future<String>> futures = new ArrayList<>(); // 创建一个Future列表来存储每个任务的结果

        for (int i = 0; i < 200; i++) { // 在一个循环中，提交每个任务到线程池
            futures.add(executorService.submit(() -> {
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
                double temperature = 0.5;

                return llmService.generateResponse(promptChats, chats, temperature);
            }));
        }

        for (Future<String> future : futures) { // 在另一个循环中，遍历Future列表并获取每个任务的结果
            String result = future.get(); // 使用Future.get()方法可以获取任务的结果，如果任务还没有完成，这个方法会阻塞直到任务完成
            System.out.println(result);
            assert result != null;
            assert result instanceof String;
        }

        executorService.shutdown(); // 关闭线程池
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
        double temperature = 0.5;

        String result = llmService.generateResponse(promptChats, chats, temperature);

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
        double temperature = 0.5;

        String result = llmService.generateResponse(promptChats, chats, temperature);

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
        double temperature = 0.5;

        String result = llmService.generateResponse(promptChats, chats, temperature);

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
