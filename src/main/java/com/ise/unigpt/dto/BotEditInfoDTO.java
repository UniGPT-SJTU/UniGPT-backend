package com.ise.unigpt.dto;

import com.ise.unigpt.model.Bot;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BotEditInfoDTO {
    private String name;
    private String avatar;
    private String description;
    private int baseModelAPI;
    private boolean isPublished;
    private String detail;
    private List<String> photos;
    private boolean isPrompted;
    private List<PromptChatDTO> promptChats;
    private List<String> promptKeys;
    private double temperature;

    public BotEditInfoDTO(Bot bot){
        this.name = bot.getName();
        this.avatar = bot.getAvatar();
        this.description = bot.getDescription();
        this.baseModelAPI = bot.getBaseModelAPI().getValue();
        this.temperature = bot.getLlmArgs().getTemperature();
        this.isPublished = bot.getIsPublished();
        this.detail = bot.getDetail();
        this.photos = bot.getPhotos();
        this.isPrompted = bot.getIsPrompted();
        this.promptChats = bot.getPromptChats().stream().map(PromptChatDTO::new).collect(Collectors.toList());
        this.promptKeys = bot.getPromptKeys();
    }
    public BotEditInfoDTO() {
        // not used
    }
}

