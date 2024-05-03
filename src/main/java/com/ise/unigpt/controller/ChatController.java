package com.ise.unigpt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private LLMService llmService;
    public ChatController() {
        llmService = new OpenAIService();
    }

    @GetMapping
    String testChat() {
        List<PromptChat> promptChatList = new ArrayList<>();
        promptChatList.add(new PromptChat(PromptChatType.SYSTEM, "你是一个++{lang}大师，精通++{lang}的所有语法和库。"));
        promptChatList.add(new PromptChat(PromptChatType.USER, "如何使用++{lang}++{question}？"));

        Map<String, String> promptList = Map.of(
            "lang", "数学",
            "question", "计算1-100的和"
        );
        List<Chat> chatList = new ArrayList<>();
        try {
            return llmService.generateResponse(promptChatList, promptList, chatList);
        } catch(Exception e) {
            return "error met";
        }
    }
}
