package com.ise.unigpt.dto;

import lombok.Data;

/**
 * 通用的响应DTO
 */
@Data
public class ResponseDTO {
    private Boolean ok;
    private String message;
    public ResponseDTO(Boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }
}
