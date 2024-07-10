package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.springframework.stereotype.Component;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

/**
 * MyChatMemoryStore
 * MemoryStore的简单实现
 */
// @Component
class MyChatMemoryStore implements ChatMemoryStore {
    private final Map<Integer, List<ChatMessage>> map = new HashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return map.getOrDefault((int) memoryId, new ArrayList<>());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        map.put((int) memoryId, messages);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove((int) memoryId);
    }
}