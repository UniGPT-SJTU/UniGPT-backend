package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class LoginErrorResponseDTO {
    private Boolean ok;
    private String message;

    public LoginErrorResponseDTO(Boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }
}
