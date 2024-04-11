package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class GetChatsErrorResponseDTO {
    private String message;

    public GetChatsErrorResponseDTO(String message) {
        this.message = message;
    }
}
