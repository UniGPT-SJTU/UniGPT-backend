package com.ise.unigpt.dto;

import com.ise.unigpt.model.Bot;
import lombok.Data;

@Data
public class BotDetailInfoDTO {

    private Integer id;
    private String name;
    private Integer authorId;
    private String author;
    private String description;
    private String detail;
    private String avatar;
    private String baseModelAPI;
    private Integer likeNumber;
    private Integer starNumber;

    public BotDetailInfoDTO(Bot bot){
        this.id = bot.getId();
        this.name = bot.getName();
        this.authorId = bot.getAuthorId();
        this.author = bot.getAuthor();
        this.description = bot.getDescription();
        this.detail = bot.getDetail();
        this.avatar = bot.getAvatar();
        this.baseModelAPI = bot.getBaseModelAPI();
        this.likeNumber = bot.getLikeNumber();
        this.starNumber = bot.getStarNumber();
    }
}
