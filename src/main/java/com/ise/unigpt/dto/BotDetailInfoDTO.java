package com.ise.unigpt.dto;

import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import lombok.Data;

import java.util.List;

@Data
public class BotDetailInfoDTO {

    private Integer id;
    private String name;
    private String creator;
    private Integer creatorId;
    private String description;
    private List<String> photos;
    private String detail;
    private String avatar;
    private String baseModelAPI;
    private Integer likeNumber;
    private Integer starNumber;
    private boolean liked;
    private boolean starred;
    private boolean asCreator;
    private List<String> promptKeys;

    public BotDetailInfoDTO(Bot bot, User user){
        this.id = bot.getId();
        this.name = bot.getName();
        this.creator = bot.getCreator().getName();
        this.creatorId = bot.getCreator().getId();
        this.description = bot.getDescription();
        this.photos = bot.getPhotos();
        this.detail = bot.getDetail();
        this.avatar = bot.getAvatar();
        this.baseModelAPI = bot.getBaseModelAPI();
        this.likeNumber = bot.getLikeNumber();
        this.starNumber = bot.getStarNumber();
        this.liked = bot.getLikeUsers().contains(user);
        this.starred = bot.getStarUsers().contains(user);
        this.asCreator = bot.getCreator().equals(user);
        this.promptKeys = bot.getPromptKeys();
    }
}
