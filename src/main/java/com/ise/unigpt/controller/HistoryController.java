package com.ise.unigpt.controller;

import com.ise.unigpt.dto.CreateChatRequestDTO;
import com.ise.unigpt.dto.GetChatsErrorResponseDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.service.ChatHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    private final ChatHistoryService service;

    public HistoryController(ChatHistoryService service) {
        this.service = service;
    }


    @GetMapping("/{id}/chats")
    public ResponseEntity<Object> getChats(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            return ResponseEntity.ok(service.getChats(id, page, pagesize));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GetChatsErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{id}/chats")
    public void createChat(@PathVariable Integer id, @RequestBody CreateChatRequestDTO dto) {
        service.createChat(id, dto.getContent(), ChatType.USER);
    }

    @GetMapping("/{historyid}/promptlist")
    public ResponseEntity<Object> getPromptList(@PathVariable Integer historyid) {
        try {
            return ResponseEntity.ok(service.getPromptList(historyid));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GetChatsErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{historyid}/promptlist")
    public ResponseDTO createPrompt(@PathVariable Integer historyid, @RequestBody List<String> promptList) {
        try {
            return service.changePromptList(historyid, promptList);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }
}
