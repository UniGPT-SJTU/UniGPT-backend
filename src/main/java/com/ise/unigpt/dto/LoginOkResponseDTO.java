package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class LoginOkResponseDTO {
    private Boolean ok;
    private String token;
    public LoginOkResponseDTO(Boolean ok, String token) {
        this.ok = ok;
        this.token = token;
    }
}
