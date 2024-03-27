package com.ise.unigpt.controller;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.repository.ChatRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatRepository repository;

    public ChatController(ChatRepository repository) {
        this.repository = repository;
    }
    @GetMapping
    public List<Chat> findAll() {
        return repository.findAll();
    }
    @GetMapping("/{id}")
    public Optional<Chat> findById(@PathVariable Integer id) {
        return repository.findById(id);
    }
}
