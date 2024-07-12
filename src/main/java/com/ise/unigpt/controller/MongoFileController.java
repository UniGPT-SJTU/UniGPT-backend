package com.ise.unigpt.controller;
import com.ise.unigpt.service.MongoFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mongo")
public class MongoFileController {

    private final MongoFileService mongoFileService;

    @Autowired
    public MongoFileController(MongoFileService mongoFileService) {
        this.mongoFileService = mongoFileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return mongoFileService.uploadFile(file);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        return mongoFileService.downloadFile(id);
    }
}

