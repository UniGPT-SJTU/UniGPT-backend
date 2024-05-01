package com.ise.unigpt.dto;

import com.google.common.annotations.VisibleForTesting;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BotDetailInfoDTO {

    private Integer id;
    private String name;
    private String creator;
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

    public BotDetailInfoDTO(Bot bot, User user){
        this.id = bot.getId();
        this.name = bot.getName();
        this.creator = bot.getCreator().getName();
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
    }

    @VisibleForTesting
    public BotDetailInfoDTO(
            Integer id,
            String name,
            String creator,
            String description,
            String detail,
            String avatar,
            String baseModelAPI,
            Integer likeNumber,
            Integer starNumber
    ) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.photos = new ArrayList<>();
        this.detail = detail;
        this.avatar = avatar;
        this.baseModelAPI = baseModelAPI;
        this.likeNumber = likeNumber;
        this.starNumber = starNumber;
    }
}
