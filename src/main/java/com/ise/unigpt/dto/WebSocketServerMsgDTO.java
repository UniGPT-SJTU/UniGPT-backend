package com.ise.unigpt.dto;

import lombok.Data;

/**
 * WebSocketMessageDTO
 * 后端向前端发送的WebSocket报文
 */
@Data
public class WebSocketServerMsgDTO {
    private String type;
    private String message;

    public WebSocketServerMsgDTO(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
