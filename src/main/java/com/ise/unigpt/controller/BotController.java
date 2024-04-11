package com.ise.unigpt.controller;


import com.ise.unigpt.dto.CreateBotRequestDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/bots")
public class BotController {

    @Autowired
    private final BotService service;

    public  BotController(BotService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<Object> getBotProfile(@PathVariable Integer id, @RequestParam String info) {
        if (info.equals("brief")) {
            return Optional.of(service.getBotBriefInfo(id));
        }   else if (info.equals("full")) {
            return Optional.of(service.getBotDetailInfo(id));
        }   else {
            return Optional.empty();
        }
    }

    @PostMapping("/")
    public void createBot(@RequestBody CreateBotRequestDTO dto){
        service.createBot(dto);
    }

    @PutMapping("/{id}")
    public void updateBot(@PathVariable Integer id, @RequestBody CreateBotRequestDTO dto) {
        service.updateBot(id, dto);
    }

    @PutMapping("/{id}/likes")
    public void likeBot(@PathVariable Integer id, @CookieValue("token") String token){
        service.likeBot(id, token);
    }

    @DeleteMapping("/{id}/likes")
    public void dislikeBot(@PathVariable Integer id, @CookieValue("token") String token) {
        service.dislikeBot(id, token);
    }

    @PutMapping("/{id}/stars")
    public void starBot(@PathVariable Integer id, @CookieValue("token") String token){
        service.starBot(id, token);
    }

    @DeleteMapping("/{id}/stars")
    public void unstarBot(@PathVariable Integer id, @CookieValue("token") String token){
        service.unstarBot(id, token);
    }


}
