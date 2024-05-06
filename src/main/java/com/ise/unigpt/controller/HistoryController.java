package com.ise.unigpt.controller;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.service.ChatHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
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
            @RequestParam(defaultValue = "20") Integer pagesize,
            @CookieValue(value = "token") String token) {
        try {
            return ResponseEntity.ok(service.getChats(id, page, pagesize, token));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GetChatsErrorResponseDTO(e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GetChatsErrorResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/{historyid}/chats")
    public ResponseEntity<ResponseDTO> createChat(
            @PathVariable Integer historyid,
            @CookieValue(value = "token") String token,
            @RequestBody CreateChatRequestDTO dto) {
        try {
            service.createChat(historyid, dto.getContent(), ChatType.USER, token);
            return ResponseEntity.ok(new ResponseDTO(true, "Chat created"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
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
    public ResponseEntity<ResponseDTO> createPrompt(
            @PathVariable Integer historyid,
            @RequestBody List<PromptDTO> promptList) {
        try {
            service.updatePromptList(historyid, promptList);
            return ResponseEntity.ok(new ResponseDTO(true, "Prompt changed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
