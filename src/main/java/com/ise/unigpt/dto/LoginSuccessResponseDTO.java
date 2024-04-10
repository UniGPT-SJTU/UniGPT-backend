package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class LoginSuccessResponseDTO {
    private Boolean ok;
    private String token;
    public LoginSuccessResponseDTO(Boolean ok, String token) {
        this.ok = ok;
        this.token = token;
    }
}
