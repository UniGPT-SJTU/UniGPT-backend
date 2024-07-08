package com.ise.unigpt.dto;

import java.util.List;

import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.User;

import lombok.Data;

@Data
public class PluginEditInfoDTO {
    private Integer id;
    private String name;
    private String creator;
    private Integer creatorId;
    private String description;
    private List<String> photos;
    private String detail;
    private String avatar;
    private boolean asCreator;
    private boolean asAdmin;


    public PluginEditInfoDTO() {
        // only for test
    }
    public PluginEditInfoDTO(Plugin plugin, User user) {
        this.id = plugin.getId();
        this.name = plugin.getName();
        this.creator = plugin.getCreator().getName();
        this.creatorId = plugin.getCreator().getId();
        this.description = plugin.getDescription();
        this.photos = plugin.getPhotos();
        this.detail = plugin.getDetail();
        this.avatar = plugin.getAvatar();
        this.asCreator = plugin.getCreator().equals(user);
        this.asAdmin = user.getAsAdmin();
    }
}
