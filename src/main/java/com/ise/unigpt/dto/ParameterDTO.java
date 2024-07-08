package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class ParameterDTO {

    String name;
    String type;
    String description;

    public ParameterDTO(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
