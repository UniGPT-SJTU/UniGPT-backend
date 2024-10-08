package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.KnowledgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;
    public KnowledgeController(
            KnowledgeService knowledgeService
    ) {
        this.knowledgeService = knowledgeService;
    }

    // 上传文件到 id 为 {id} 的 bot
    @PostMapping("/upload/{id}")
    public ResponseEntity<ResponseDTO> uploadFile(
            @PathVariable Integer id,
            @CookieValue("token") String token,
            @RequestParam("file") MultipartFile file
    ){
        try{
            return ResponseEntity.ok(knowledgeService.uploadFile(id, token, file));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
        catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    // @GetMapping("/query/{id}")
    // public ResponseEntity<ResponseDTO> queryKnowledge(
    //         @PathVariable Integer id,
    //         @RequestParam String queryText,
    //         @RequestParam(defaultValue = "1") Integer maxResults
    // ){
    //     try{
    //         return ResponseEntity.ok(new ResponseDTO(true, knowledgeService.queryKnowledge(id, queryText, maxResults).toString()));
    //     }
    //     catch (NoSuchElementException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(new ResponseDTO(false, e.getMessage()));
    //     }
    // }
}
