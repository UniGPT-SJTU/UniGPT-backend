package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class UpdateUserInfoRequestDTO {
    private String name;
    private String avatar;
    private String description;
    private String canvasUrl;
}
