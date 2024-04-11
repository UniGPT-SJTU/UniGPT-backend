package com.ise.unigpt.dto;

import lombok.Data;

/**
 * 通用的错误响应DTO
 */
@Data
public class ErrorResponseDTO {
    private String message;
    public ErrorResponseDTO(String message) {
        this.message = message;
    }
}
