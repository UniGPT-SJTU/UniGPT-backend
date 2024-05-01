package com.ise.unigpt.service;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LLMService {
    public String generateResponse(List<PromptChat> promptChats, List<String> promptKeys, List<Chat> chats);
}
