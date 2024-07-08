package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class PluginBriefInfoDTO {
    private Integer id;
    private String name;
    private String description;
    private String avatar;
    private boolean asCreator;
    private boolean asAdmin;

    public PluginBriefInfoDTO(Integer id, String name, String description, String avatar, boolean asCreator,
            boolean asAdmin) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatar = avatar;
        this.asCreator = asCreator;
        this.asAdmin = asAdmin;
    }
}
