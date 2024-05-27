package com.ise.unigpt.service;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用的大模型服务接口，供WebSocket部分后端开发者调用，使用样例见controller/ChatController.java
 */
@Service
public interface LLMService {
    /**
     * @brief 生成LLM的回复
     * 
     * @param promptChats 对话历史的提示对话（已经嵌入了用户填写的表单）
     * @param chats 用户与机器人的所有对话
     * @return LLM基于对话补全生成的回复内容
     * @throws Exception 抛出异常
     */
    public String generateResponse(List<PromptChat> promptChats, List<Chat> chats) throws Exception;
}
