package com.ise.unigpt.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

/**
 * WebSocketMessageDTO
 * 后端向前端发送的WebSocket报文
 */
@Data
public class WebSocketMessageDTO {
    private String type;
    private String message;

    public WebSocketMessageDTO(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(this);
            return json;
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
