package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final AuthService authService;
    public KnowledgeController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<ResponseDTO> uploadFile(
            @PathVariable Integer id,
            @CookieValue("token") String token,
            @RequestParam("file") MultipartFile file
    ){

    }
}
