package com.ise.unigpt.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CanvasEventDTO {
    String name;
    String description;
    Instant ddlTime;

    public CanvasEventDTO(String name, String description, Instant ddlTime) {
        this.name = name;
        this.description = description;
        this.ddlTime = ddlTime;
    }
}
