package com.ise.unigpt.model;

import com.ise.unigpt.dto.BotEditInfoDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "bot")
public class Bot {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "description")
    private String description;

    @Column(name = "base_model_api")
    private String baseModelAPI;

    @Column(name = "is_published")
    private boolean isPublished;

    @Column(name = "detail")
    private String detail;

    @ElementCollection
    private List<String> photos;

    @Column(name = "is_prompted")
    private boolean isPrompted;

    @OneToMany
    private List<PromptChat> promptChats;

    @ElementCollection
    private List<String> promptKeys;

    @Column(name = "like_number")
    private int likeNumber;

    @Column(name = "star_number")
    private int starNumber;

    @ManyToMany(mappedBy = "likeBots")
    private List<User> likeUsers;

    @ManyToMany(mappedBy = "starBots")
    private List<User> starUsers;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany
    private List<Comment> comments;

    public Bot(BotEditInfoDTO dto, User creator) {
        this.name = dto.getName();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.baseModelAPI = dto.getBaseModelAPI();
        this.isPublished = dto.isPublished();
        this.detail = dto.getDetail();
        this.photos = dto.getPhotos();
        this.isPrompted = dto.isPrompted();
        this.promptKeys = dto.getPromptKeys();
        this.likeNumber = 0;
        this.starNumber = 0;
        this.likeUsers = new ArrayList<>();
        this.starUsers = new ArrayList<>();
        this.creator = creator;
        this.comments = new ArrayList<>();
    }
    // TODO: 将CreateBotRequestDTO和UpdateBotRequestDTO合并为一个DTO
    public void updateInfo(BotEditInfoDTO dto) {
        this.name = dto.getName();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.baseModelAPI = dto.getBaseModelAPI();
        this.isPublished = dto.isPublished();
        this.detail = dto.getDetail();
        this.photos = dto.getPhotos();
        this.isPrompted = dto.isPrompted();
        this.promptKeys = dto.getPromptKeys();
    }
    public Bot() {
        // not used
    }
}
