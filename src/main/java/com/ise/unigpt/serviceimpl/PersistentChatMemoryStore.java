package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.ise.unigpt.model.Memory;
import com.ise.unigpt.model.MemoryItem;
import com.ise.unigpt.repository.MemoryRepository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final MemoryRepository memoryRepository;

    private final Logger log = org.slf4j.LoggerFactory.getLogger(PersistentChatMemoryStore.class);

    public PersistentChatMemoryStore(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Memory memory = memoryRepository.findById((Integer) memoryId).get();
        List<ChatMessage> chatMessages = memory
                .getMemoryItems()
                .stream()
                .map(memoryItem -> memoryItem.toChatMessage())
                .collect(Collectors.toList());

        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Memory memory = memoryRepository.findById((Integer) memoryId).get();
        List<MemoryItem> memoryItems = messages
                .stream()
                .map(message -> new MemoryItem(message, memory))
                .collect(Collectors.toList());

        memory.setMemoryItems(memoryItems);
        memoryRepository.save(memory);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        Memory memory = memoryRepository.findById((Integer) memoryId).get();
        memory.setMemoryItems(new ArrayList<>());
        memoryRepository.save(memory);
    }

}