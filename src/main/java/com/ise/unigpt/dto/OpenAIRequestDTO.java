package com.ise.unigpt.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenAIRequestDTO {
    private String model;
    private List<OpenAIMessageDTO> messages;
    private double temperature;
    
    public OpenAIRequestDTO(String model, List<OpenAIMessageDTO> messages, double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

}
