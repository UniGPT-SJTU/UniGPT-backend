package com.ise.unigpt.controller;

import com.google.gson.Gson;
import com.ise.unigpt.dto.FileUploadOkResponseDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.AuthService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final AuthService authService;


    public FileUploadController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> uploadFile(@CookieValue("token") String token,
                                                  @RequestParam("file") MultipartFile file
    ) {
        try {
            logger.info("Uploading file: " + file.getOriginalFilename());
            String originalFilename = file.getOriginalFilename();
            String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            File tempFile = File.createTempFile(filenameWithoutExtension + "-", extension);

            file.transferTo(tempFile);
            
            // 使用Unirest发送请求给图片服务器
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("http://10.119.12.131:10339/upload")
            .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
            .header("Content-Type", "multipart/form-data")
            .field("file", tempFile)
            .asString();

            if(response.getStatus() != 200) {
                throw new Exception("Failed to upload file");
            }

            System.out.println(response.getBody());

            Gson gson = new Gson();
            FileUploadOkResponseDTO dto = gson.fromJson(response.getBody(), FileUploadOkResponseDTO.class);

            String imageUrl = dto.getUrl();

            // 返回图片URL给前端
            return ResponseEntity.ok(new ResponseDTO(true, imageUrl));

        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Failed to upload file"));
        }
    }
}
