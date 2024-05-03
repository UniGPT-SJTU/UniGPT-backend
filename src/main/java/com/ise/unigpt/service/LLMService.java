package com.ise.unigpt.service;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LLMService {
    public String generateResponse(List<PromptChat> promptChats, Map<String, String> promptList, List<Chat> chats) throws Exception;
}
