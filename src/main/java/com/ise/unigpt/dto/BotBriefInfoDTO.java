package com.ise.unigpt.dto;

import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;

import lombok.Data;

@Data
public class BotBriefInfoDTO {

    private Integer id;
    private String name;
    private String description;
    private String avatar;
    private boolean asCreator;
    private boolean asAdmin;

    public BotBriefInfoDTO(Integer id, String name, String description, String avatar, boolean asCreator,
            boolean asAdmin) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatar = avatar;
        this.asCreator = asCreator;
        this.asAdmin = asAdmin;
    }

    public BotBriefInfoDTO(Bot bot, User user) {
        this.id = bot.getId();
        this.name = bot.getName();
        this.description = bot.getDescription();
        this.avatar = bot.getAvatar();
        this.asCreator = bot.getCreator().equals(user);
        this.asAdmin = user.getAsAdmin();
    }
}
