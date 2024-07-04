package com.ise.unigpt.service;

import com.ise.unigpt.dto.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;

public interface KnowledgeService {
    /*
    创建者 / 管理员为编号为 id 的机器人添加 file 到知识库
    */
    ResponseDTO uploadFile(Integer id, String token, MultipartFile file) throws AuthenticationException;
}
