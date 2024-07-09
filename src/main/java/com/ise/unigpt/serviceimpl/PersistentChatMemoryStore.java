package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ise.unigpt.model.Memory;
import com.ise.unigpt.model.MemoryItem;
import com.ise.unigpt.repository.MemoryRepository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

// @Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final MemoryRepository memoryRepository;

    public PersistentChatMemoryStore(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> chatMessages = memoryRepository.findById((Integer) memoryId)
                .get()
                .getMemoryItems()
                .stream()
                .map(memoryItem -> memoryItem.toChatMessage())
                .collect(Collectors.toList());

        // System.out.println("getMessages: chatMessages.size == " +
        // chatMessages.size());
        // for (ChatMessage chatMessage : chatMessages) {
        // System.out.println("getMessages: chatMessage == " + chatMessage);
        // }
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Memory memory = memoryRepository.findById((Integer) memoryId).get();
        // System.out.println("updateMessages: messages.size == " + messages.size());
        // for (ChatMessage message : messages) {
        // System.out.println("updateMessages: message == " + message);
        // }
        List<MemoryItem> memoryItems = messages
                .stream()
                .map(chatMessage -> new MemoryItem(chatMessage, memory))
                .toList();
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