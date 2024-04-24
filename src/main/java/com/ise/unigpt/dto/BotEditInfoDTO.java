package com.ise.unigpt.dto;

import com.ise.unigpt.model.Bot;
import lombok.Data;

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
}
