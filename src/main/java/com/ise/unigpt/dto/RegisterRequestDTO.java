package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    String username;
    String email;
    String password;
    String avatar;
    String description;
    public RegisterRequestDTO(String username, String email, String password, String avatar, String description) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.description = description;
    }
}
