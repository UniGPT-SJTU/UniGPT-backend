package com.ise.unigpt.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.annotations.SerializedName;
import com.ise.unigpt.model.Bot;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BotEditInfoDTO {
    private String name;
    private String avatar;
    private String description;
    private String baseModelAPI;

    @SerializedName("published")
    private boolean isPublished;

    private String detail;
    private List<String> photos;

    @SerializedName("prompted")
    private boolean isPrompted;

    private List<PromptChatDTO> promptChats;
    private List<String> promptKeys;

    public BotEditInfoDTO(Bot bot){
        this.name = bot.getName();
        this.avatar = bot.getAvatar();
        this.description = bot.getDescription();
        this.baseModelAPI = bot.getBaseModelAPI();
        this.isPublished = bot.isPublished();
        this.detail = bot.getDetail();
        this.photos = bot.getPhotos();
        this.isPrompted = bot.isPrompted();
        this.promptChats = bot.getPromptChats().stream().map(PromptChatDTO::new).collect(Collectors.toList());
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

