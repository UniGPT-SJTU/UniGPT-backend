package com.ise.unigpt.service;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通用的大模型服务接口，供WebSocket部分后端开发者调用，使用样例见controller/ChatController.java
 */
@Service
public interface LLMService {
    /**
     * @brief 生成LLM的回复
     * 
     * @param promptChats 机器人在创建时预设的对话模板
     * @param promptList 用户在使用机器人时填入的提示词键值对
     * @param chats 用户与机器人的所有对话
     * @return LLM基于对话补全生成的回复内容
     * @throws Exception 抛出异常
     */
    public String generateResponse(List<PromptChat> promptChats, Map<String, String> promptList, List<Chat> chats) throws Exception;
}
