package com.ise.unigpt.controller;

import com.ise.unigpt.model.ChatHistory;
import com.ise.unigpt.repository.ChatHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/history")
public class ChatHistoryController {
    private final ChatHistoryRepository repository;

    public ChatHistoryController(ChatHistoryRepository repository) {
        this.repository = repository;
    }
    @GetMapping
    public List<ChatHistory> findAll() {
        return repository.findAll();
    }
    @GetMapping("/{id}")
    public Optional<ChatHistory> findById(@PathVariable Integer id) {
        return repository.findById(id);
    }

}
