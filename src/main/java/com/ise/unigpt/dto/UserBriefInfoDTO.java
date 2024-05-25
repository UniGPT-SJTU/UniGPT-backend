package com.ise.unigpt.dto;

import com.ise.unigpt.model.User;
import lombok.Data;

@Data
public class UserBriefInfoDTO {

    private Integer id;
    private String name;
    private String avatar;
    private String description;

    public UserBriefInfoDTO(Integer id, String name, String avatar, String description) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.description = description;
    }

    public UserBriefInfoDTO() {
    }

    @Override
    public String toString() {
        return "UserBriefInfoDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public UserBriefInfoDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.description = user.getDescription();
    }
}
