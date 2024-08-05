package com.ise.unigpt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.FunctionGraphService;
import com.ise.unigpt.service.InvokeFunctionSolutionService;

@RestController
@RequestMapping("/api/functiongraph")
public class FunctionGraphController {

    private final FunctionGraphService functionGraphService;
    private final InvokeFunctionSolutionService invokeFunctionSolutionService;

    public FunctionGraphController(FunctionGraphService functionGraphService, InvokeFunctionSolutionService invokeFunctionSolutionService) {
        this.functionGraphService = functionGraphService;
        this.invokeFunctionSolutionService = invokeFunctionSolutionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> uploadFunction(@CookieValue(value = "token") String token) {
        try {
            functionGraphService.uploadFunction();

            return ResponseEntity.ok(new ResponseDTO(true, "Function uploaded successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ResponseDTO(false, "Failed to upload function"));
        }
    }

    @PostMapping("/invoke")
    public ResponseEntity<ResponseDTO> invokeFunction(@CookieValue(value = "token") String token) {
        try {
            invokeFunctionSolutionService.CallFunction(null);

            return ResponseEntity.ok(new ResponseDTO(true, "Function invoked successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ResponseDTO(false, "Failed to invoke function"));
        }
    }
}
