package com.ise.unigpt.service;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatRepository repository;

    @Autowired
    public ChatService(ChatRepository repository) {
        this.repository = repository;
    }
    public List<Chat> getAllChats() {
        return repository.findAll();
    }
    public Chat getChatById(Integer id) {
        return repository.getReferenceById(id);
    }
}
