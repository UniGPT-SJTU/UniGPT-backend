package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class LoginFailureResponseDTO {
    private Boolean ok;
    private String message;

    public LoginFailureResponseDTO(Boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }
}
