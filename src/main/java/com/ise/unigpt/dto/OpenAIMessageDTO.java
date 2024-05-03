package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class OpenAIMessageDTO {
    private String role;
    private String content;
    public OpenAIMessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
