package com.ise.unigpt.model;

import com.ise.unigpt.dto.BotEditInfoDTO;
import com.ise.unigpt.parameters.LLMArgs.LLMArgs;

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
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Embedded
    private LLMArgs llmArgs;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "detail", columnDefinition = "VARCHAR(255)")
    private String detail;

    @ElementCollection
    private List<String> photos;

    @Column(name = "is_prompted")
    private Boolean isPrompted;

    @OneToMany(fetch = FetchType.EAGER)
    private List<PromptChat> promptChats;

    @ElementCollection
    @Column(name = "promptKeys", columnDefinition = "VARCHAR(255)")
    private List<String> promptKeys;

    @Column(name = "like_number")
    private Integer likeNumber;

    @Column(name = "star_number")
    private Integer starNumber;

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

        this.llmArgs = LLMArgs.builder()
            .baseModelType(BaseModelType.fromValue(dto.getBaseModelAPI()))
            .temperature(dto.getTemperature()).build();
    }

    public void updateInfo(BotEditInfoDTO dto) {
        this.name = dto.getName();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.isPublished = dto.isPublished();
        this.detail = dto.getDetail();
        this.photos = dto.getPhotos();
        this.isPrompted = dto.isPrompted();
        this.promptKeys = dto.getPromptKeys();

        this.llmArgs = LLMArgs
                .builder()
                .baseModelType(BaseModelType.fromValue(dto.getBaseModelAPI()))
                .temperature(dto.getTemperature())
                .build();
    }

    public Bot() {
        // not used
        this.likeUsers = new ArrayList<>();
        this.starUsers = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.creator = new User();
    }
}
