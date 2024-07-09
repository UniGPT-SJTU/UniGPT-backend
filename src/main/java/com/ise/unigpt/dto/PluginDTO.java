package com.ise.unigpt.dto;

import com.ise.unigpt.model.Plugin;

import lombok.Data;

@Data
public class PluginDTO {

    private Integer id;
    private String name;
    private String description;
    private String detail;
    private String avatar;

    public PluginDTO() {
        // only for test
    }

    public PluginDTO(Integer id, String name, String description, String detail, String avatar) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.detail = detail;
        this.avatar = avatar;
    }

    public PluginDTO(Plugin plugin) {
        this.id = plugin.getId();
        this.name = plugin.getName();
        this.description = plugin.getDescription();
        this.detail = plugin.getDetail();
        this.avatar = plugin.getAvatar();
    }
}
