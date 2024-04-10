package com.ise.unigpt.controller;

import com.ise.unigpt.dto.CreateChatDTO;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    @Autowired
    private final ChatHistoryService service;

    public HistoryController(ChatHistoryService service) {
        this.service = service;
    }


    @GetMapping("/{id}/chats")
    public List<Chat> findAllChats(@PathVariable Integer id) {
        return service.getAllChats(id);
    }

    @PostMapping("/{id}/chats")
    public void createChat(@PathVariable Integer id, @RequestBody CreateChatDTO dto) {
        service.addNewChatToHistory(id, dto.getContent(), ChatType.USER);
    }
}
