package com.ise.unigpt.controller;

import com.ise.unigpt.dto.CreateChatRequestDTO;
import com.ise.unigpt.dto.GetChatsErrorResponseDTO;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    @Autowired
    private final ChatHistoryService service;

    public HistoryController(ChatHistoryService service) {
        this.service = service;
    }


    @GetMapping("/{id}/chats")
    public ResponseEntity<Object> getChats(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.getChats(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GetChatsErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{id}/chats")
    public void createChat(@PathVariable Integer id, @RequestBody CreateChatRequestDTO dto) {
        service.createChat(id, dto.getContent(), ChatType.USER);
    }
}
