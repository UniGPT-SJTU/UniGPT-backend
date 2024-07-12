package com.ise.unigpt.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MongoFileService {
    String uploadFile(MultipartFile file);

    ResponseEntity<byte[]> downloadFile(String id) ;
}