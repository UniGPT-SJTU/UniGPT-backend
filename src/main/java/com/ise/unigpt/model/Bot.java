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

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "base_model_api")
    private BaseModelType baseModelAPI;

    @Column(name = "is_published")
    private boolean isPublished;

    @Column(name = "detail", columnDefinition = "LONGTEXT")
    private String detail;

    @ElementCollection
    private List<String> photos;

    @Column(name = "is_prompted")
    private boolean isPrompted;

    @OneToMany(fetch = FetchType.EAGER)
    private List<PromptChat> promptChats;

    @ElementCollection
    @Column(name = "promptKeys", columnDefinition = "LONGTEXT")
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

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Bot(BotEditInfoDTO dto, User creator) {
        this.name = dto.getName();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.baseModelAPI = BaseModelType.fromValue(dto.getBaseModelAPI());
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

    public void updateInfo(BotEditInfoDTO dto) {
        this.name = dto.getName();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.baseModelAPI = BaseModelType.fromValue(dto.getBaseModelAPI());
        this.isPublished = dto.isPublished();
        this.detail = dto.getDetail();
        this.photos = dto.getPhotos();
        this.isPrompted = dto.isPrompted();
        this.promptKeys = dto.getPromptKeys();
    }

    public Bot() {
        // not used
        this.likeUsers = new ArrayList<>();
        this.starUsers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }
}
