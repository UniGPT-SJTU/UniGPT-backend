package com.ise.unigpt.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenAIRequestDTO {
    private String model;
    private List<OpenAIMessageDTO> messages;

    public OpenAIRequestDTO(String model, List<OpenAIMessageDTO> messages) {
        this.model = model;
        this.messages = messages;
    }

}
