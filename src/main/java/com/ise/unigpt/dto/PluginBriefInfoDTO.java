package com.ise.unigpt.dto;

import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.User;

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

    public PluginBriefInfoDTO(Plugin plugin, User user) {
        this.id = plugin.getId();
        this.name = plugin.getName();
        this.description = plugin.getDescription();
        this.avatar = plugin.getAvatar();
        this.asCreator = plugin.getCreator().equals(user);
        this.asAdmin = user.getAsAdmin();
    }
}
