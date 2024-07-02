package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import com.ise.unigpt.repository.HistoryRepository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final HistoryRepository historyRepository;

    public PersistentChatMemoryStore(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> chatMessages = historyRepository.findById((Integer) memoryId)
                .get()
                .getChats()
                .stream()
                .map(chat -> chat.toChatMessage())
                .toList();

        System.out.println("getMessages: chatMessages.size == " + chatMessages.size());
        for (ChatMessage chatMessage : chatMessages) {
            System.out.println("getMessages: chatMessage == " + chatMessage);
        }
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        History history = historyRepository.findById((Integer) memoryId).get();
        System.out.println("updateMessages: messages.size == " + messages.size());
        for (ChatMessage message : messages) {
            System.out.println("updateMessages: message == " + message);
        }
        List<Chat> chats = messages
                .stream()
                .map(chatMessage -> new Chat(history, chatMessage))
                .toList();
        history.setChats(chats);
        historyRepository.save(history);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        History history = historyRepository.findById((Integer) memoryId).get();
        history.setChats(new ArrayList<>());
        historyRepository.save(history);
    }

}