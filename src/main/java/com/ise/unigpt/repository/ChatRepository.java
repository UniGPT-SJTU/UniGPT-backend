package com.ise.unigpt.repository;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepository {
    private final List<Chat> chatList = new ArrayList<>();
    public ChatRepository() {
    }
    public List<Chat> findAll() {
        return chatList;
    }
    public Optional<Chat> findById(Integer id) {
        return chatList.stream().filter(chat -> chat.getId().equals(id)).findFirst();
    }


    @PostConstruct
    private void init() {
        chatList.add(new Chat(0,0, ChatType.USER, "Hello, what's your name?"));
        chatList.add(new Chat(1,0,ChatType.BOT, "Hi!My name is ChatGPT.What can I do for you ?"));
    }
}
