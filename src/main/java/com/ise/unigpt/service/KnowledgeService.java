package com.ise.unigpt.service;

import com.ise.unigpt.dto.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;

public interface KnowledgeService {
    ResponseDTO uploadFile(Integer id, String token, MultipartFile file) throws AuthenticationException;
}
