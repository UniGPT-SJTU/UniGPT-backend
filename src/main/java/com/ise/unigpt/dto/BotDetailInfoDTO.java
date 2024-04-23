package com.ise.unigpt.dto;

import com.google.common.annotations.VisibleForTesting;
import com.ise.unigpt.model.Bot;
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

    public BotDetailInfoDTO(Bot bot){
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
