package com.ise.unigpt.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.service.DockerService;

@RestController
@RequestMapping("/api/function")
public class DockerRunFunction {
    
    DockerService funcExecuteService = new DockerService();

    @RequestMapping("/invoke")
    public ResponseEntity<String> invokeFunction() {
        Object result = funcExecuteService.invokeFunction("add", List.of(1, 2));
        return ResponseEntity.ok(result.toString());
    }
}
