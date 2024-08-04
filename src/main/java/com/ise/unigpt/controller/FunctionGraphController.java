package com.ise.unigpt.controller;

import com.ise.unigpt.service.FunctionGraphService;
import com.ise.unigpt.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.huaweicloud.sdk.functiongraph.v2.*;
import com.huaweicloud.sdk.functiongraph.v2.model.*;

@RestController
@RequestMapping("/api/functiongraph")
public class FunctionGraphController {

    private final FunctionGraphService functionGraphService;

    public FunctionGraphController(FunctionGraphService functionGraphService) {
        this.functionGraphService = functionGraphService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> uploadFunction(@CookieValue(value = "token") String token) {
        try {
            functionGraphService.UploadFunction();

            return ResponseEntity.ok(new ResponseDTO(true, "Function uploaded successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ResponseDTO(false, "Failed to upload function"));
        }
    }
}
