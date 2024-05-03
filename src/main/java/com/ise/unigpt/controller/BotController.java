package com.ise.unigpt.controller;

import com.ise.unigpt.dto.BotEditInfoDTO;
import com.ise.unigpt.dto.PromptDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.dto.CommentRequestDTO;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    private final BotService service;

    public BotController(BotService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> getBots(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "latest") String order,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            return ResponseEntity.ok(service.getBots(q, order, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBotProfile(@PathVariable Integer id, @RequestParam String info,
            @CookieValue("token") String token) {
        try {
            return switch (info) {
                case "brief" -> ResponseEntity.ok(service.getBotBriefInfo(id));
                case "detail" -> ResponseEntity.ok(service.getBotDetailInfo(id, token));
                case "edit" -> ResponseEntity.ok(service.getBotEditInfo(id, token));
                default -> ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(false, "Invalid info parameter"));
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createBot(@RequestBody BotEditInfoDTO dto, @CookieValue("token") String token) {
        try {
            return ResponseEntity.ok(service.createBot(dto, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBot(@PathVariable Integer id, @RequestBody BotEditInfoDTO dto, @CookieValue("token") String token) {
        try {
            return ResponseEntity.ok(service.updateBot(id, dto, token));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/likes")
    public ResponseEntity<Object> likeBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return ResponseEntity.ok(service.likeBot(id, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Object> dislikeBot(@PathVariable Integer id, @CookieValue("token") String token) {
        try {
            return ResponseEntity.ok(service.dislikeBot(id, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/stars")
    public ResponseEntity<Object> starBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return ResponseEntity.ok(service.starBot(id, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/stars")
    public ResponseEntity<Object> unstarBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return ResponseEntity.ok(service.unstarBot(id, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<Object> getBotHistory(@PathVariable Integer id, @CookieValue("token") String token,
            @RequestParam Integer page, @RequestParam Integer pagesize) {
        try {
            return ResponseEntity.ok(service.getBotHistory(id, token, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{botid}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Integer botid,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            return ResponseEntity.ok(service.getComments(botid, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/{id}/histories")
    public ResponseEntity<Object> createBotHistory(@PathVariable Integer id, @CookieValue("token") String token, @RequestBody List<PromptDTO> promptList) {
        try {
            return ResponseEntity.ok(service.createBotHistory(id, token, promptList));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/{botid}/comments")
    public ResponseEntity<Object> createComment(@PathVariable Integer botid,
                                     @CookieValue("token") String token,
                                     @RequestBody CommentRequestDTO request) {
        try {
            return ResponseEntity.ok(service.createComment(botid, token, request.getContent()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
