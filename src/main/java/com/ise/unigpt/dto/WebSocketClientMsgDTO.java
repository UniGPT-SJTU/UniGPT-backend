package com.ise.unigpt.dto;

import lombok.Data;

/**
 * WebSocketClientMsgDTO
 * 前端向后端发送的WebSocket报文
 */
@Data
public class WebSocketClientMsgDTO {
    private Boolean userAsk;
    private Boolean cover;
    private String chatContent;
}
