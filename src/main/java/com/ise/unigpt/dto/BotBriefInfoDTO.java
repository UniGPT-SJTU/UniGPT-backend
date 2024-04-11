package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class BotBriefInfoDTO {
    private Integer id;
    private String name;
    private String description;
    private String avatar;

    public BotBriefInfoDTO(Integer id, String name, String description, String avatar) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatar = avatar;
    }
}
