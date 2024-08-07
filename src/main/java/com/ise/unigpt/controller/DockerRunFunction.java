package com.ise.unigpt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.service.DockerService;
import com.ise.unigpt.service.FunGraphService;

@RestController
@RequestMapping("/api/function")
public class DockerRunFunction {

    private final DockerService dockerService;
    private final FunGraphService funGraphService;

    public DockerRunFunction(DockerService dockerService, FunGraphService funGraphService) {
        this.dockerService = dockerService;
        this.funGraphService = funGraphService;
    }

    @RequestMapping("/invoke/docker")
    public ResponseEntity<String> invokeFunction(@RequestParam String moduleName, @RequestParam String param) {
        Object result = dockerService.invokeFunction("func", moduleName, "handler", List.of(param));
        return ResponseEntity.ok(result.toString());
    }

    @RequestMapping("/invoke/fungraph")
    public ResponseEntity<String> invokeFunGraphFunction(@RequestParam String moduleName, @RequestParam String param, @RequestParam String urn) {
        Object result = funGraphService.invokeFunction("func", moduleName, "handler", List.of(param), urn);
        return ResponseEntity.ok(result.toString());
    }
}
