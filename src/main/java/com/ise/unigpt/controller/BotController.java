package com.ise.unigpt.controller;


import com.ise.unigpt.dto.CreateBotRequestDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.model.CommentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> getBotProfile(@PathVariable Integer id, @RequestParam String info) {
        try {
            if (info.equals("brief")) {
                return ResponseEntity.ok(service.getBotBriefInfo(id));
            } else if (info.equals("detail")) {
                return ResponseEntity.ok(service.getBotDetailInfo(id));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(false, "Invalid info parameter"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseDTO createBot(@RequestBody CreateBotRequestDTO dto){
        try{
            return service.createBot(dto);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseDTO updateBot(@PathVariable Integer id, @RequestBody CreateBotRequestDTO dto) {
        try {
            return service.updateBot(id, dto);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @PutMapping("/{id}/likes")
    public ResponseDTO likeBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return service.likeBot(id, token);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/likes")
    public ResponseDTO dislikeBot(@PathVariable Integer id, @CookieValue("token") String token) {
        try {
            return service.dislikeBot(id, token);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @PutMapping("/{id}/stars")
    public ResponseDTO starBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return service.starBot(id, token);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/stars")
    public ResponseDTO unstarBot(@PathVariable Integer id, @CookieValue("token") String token){
        try {
            return service.unstarBot(id, token);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Object> getBotHistory(@PathVariable Integer id, @CookieValue("token") String token, @RequestParam Integer page, @RequestParam Integer pageSize) {
        try {
            return ResponseEntity.ok(service.getBotHistory(id, token, page, pageSize));
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

    @PostMapping("/{id}/history")
    public ResponseDTO addChatHistory(@PathVariable Integer id, @CookieValue("token") String token, @RequestBody String content) {
        try {
            return service.addChatHistory(id, token, content);
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    @PostMapping("/{botid}/comments")
    public ResponseDTO createComment(@PathVariable Integer botid,
                                     @CookieValue("token") String token,
                                     @RequestBody CommentRequest request) {
        try {
            return service.createComment(botid, token, request.getContent());
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }
}
