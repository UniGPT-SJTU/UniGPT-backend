package com.ise.unigpt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.dto.PluginCreateDTO;
import com.ise.unigpt.dto.PluginEditInfoDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.PluginService;

@RestController
@RequestMapping("/api/plugin")
public class PluginController {
    
    private final PluginService pluginService;

    public PluginController(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    @GetMapping
    public ResponseEntity<Object> getPlugins(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "latest") String order,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            return ResponseEntity.ok(pluginService.getPlugins(q, order, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{pluginid}")
    public ResponseEntity<Object> getPluginInfo(
            @RequestParam Integer pluginid,
            @RequestParam String token) {
        try {
            return ResponseEntity.ok(pluginService.getPluginInfo(pluginid, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/edit/{pluginid}")
    public ResponseEntity<Object> getPluginEditInfo(
            @RequestParam Integer pluginid,
            @RequestParam String token) {
        try {
            return ResponseEntity.ok(pluginService.getPluginEditInfo(pluginid, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPlugin(
            @RequestParam PluginCreateDTO dto,
            @RequestParam String token) {
        try {
            return ResponseEntity.ok(pluginService.createPlugin(dto, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
