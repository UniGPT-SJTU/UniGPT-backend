package com.ise.unigpt.dto;

import com.google.common.annotations.VisibleForTesting;
import com.ise.unigpt.model.Bot;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BotEditInfoDTO {
    private String name;
    private String avatar;
    private String description;
    private String baseModelAPI;
    private boolean isPublished;
    private String detail;
    private List<String> photos;
    private boolean isPrompted;
    private List<PromptChatDTO> promptChats;
    private List<String> promptKeys;

    public BotEditInfoDTO(Bot bot){
        // TODO: 需要审查
        this.name = bot.getName();
        this.avatar = bot.getAvatar();
        this.description = bot.getDescription();
        this.baseModelAPI = bot.getBaseModelAPI();
        this.isPublished = bot.isPublished();
        this.detail = bot.getDetail();
        this.photos = bot.getPhotos();
        this.isPrompted = bot.isPrompted();
        bot.getPromptChats().forEach(promptChat -> this.promptChats.add(new PromptChatDTO(promptChat)));
        this.promptKeys = bot.getPromptKeys();
    }
    public BotEditInfoDTO() {
        // not used
    }
    @VisibleForTesting
    public BotEditInfoDTO(
            String name,
            String avatar,
            String description,
            String baseModelAPI,
            boolean isPublished,
            String detail,
            boolean isPrompted
    ) {
        this.name = name;
        this.avatar = avatar;
        this.description = description;
        this.baseModelAPI = baseModelAPI;
        this.isPublished = isPublished;
        this.detail = detail;
        this.photos = new ArrayList<>();
        this.isPrompted = isPrompted;
        this.promptChats = new ArrayList<>();
        this.promptKeys = new ArrayList<>();
    }
}

