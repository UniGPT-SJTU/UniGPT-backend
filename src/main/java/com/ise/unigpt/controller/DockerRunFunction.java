package com.ise.unigpt.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.service.DockerService;

@RestController
@RequestMapping("/api/function")
public class DockerRunFunction {
    
    private final DockerService dockerService;

    public DockerRunFunction(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @RequestMapping("/invoke")
    public ResponseEntity<String> invokeFunction(@RequestParam String moduleName, @RequestParam String param) {
        Object result = dockerService.invokeFunction(moduleName,"handler", List.of(param));
        return ResponseEntity.ok(result.toString());
    }
}
