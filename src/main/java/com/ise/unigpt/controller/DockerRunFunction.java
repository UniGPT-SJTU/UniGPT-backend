package com.ise.unigpt.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.service.DockerService;

@RestController
@RequestMapping("/api/function")
public class DockerRunFunction {
    
    DockerService funcExecuteService = new DockerService();

    @RequestMapping("/invoke")
    public ResponseEntity<String> invokeFunction(@RequestParam String moduleName, @RequestParam String functionName, @PathVariable Integer param) {
        Object result = funcExecuteService.invokeFunction(moduleName,"handler", List.of(param));
        return ResponseEntity.ok(result.toString());
    }
}
