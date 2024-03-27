package com.ise.unigpt.repository;

import com.ise.unigpt.model.ChatHistory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatHistoryRepository {
    private final List<ChatHistory> chatHistoryList = new ArrayList<>();
    public ChatHistoryRepository() {
    }

    public List<ChatHistory> findAll() {
        return chatHistoryList;
    }
    public Optional<ChatHistory> findById(Integer id) {
        return chatHistoryList.stream().filter(chatHistory -> chatHistory.getId().equals(id)).findFirst();
    }


    @PostConstruct
    private void init() {
        List<Integer> chats = new ArrayList<>();
        chats.add(0);
        chats.add(1);
        ChatHistory chatHistory = new ChatHistory(0,0,0,"First Chat",chats);
        chatHistoryList.add(chatHistory);
    }
}
