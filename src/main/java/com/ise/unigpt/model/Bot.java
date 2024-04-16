package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

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

    @OneToMany
    private List<Photo> photos;

    @Column(name = "is_prompted")
    private boolean isPrompted;

    @OneToMany
    private List<Chat> promptChats;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bot")
    private List<Comment> comments;
}
