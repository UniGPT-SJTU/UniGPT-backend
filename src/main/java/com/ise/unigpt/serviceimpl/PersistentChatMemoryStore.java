package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import com.ise.unigpt.repository.HistoryRepository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final HistoryRepository historyRepository;

    public PersistentChatMemoryStore(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return historyRepository.findById((Integer) memoryId)
                .get()
                .getChats()
                .stream()
                .map(chat -> (ChatMessage) chat)
                .toList();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        History history = historyRepository.findById((Integer) memoryId).get();
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