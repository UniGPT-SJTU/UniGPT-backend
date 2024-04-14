package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "user_like_bot",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bot_id")
    )
    private List<Bot> likeBots;

    @ManyToMany
    @JoinTable(
            name = "user_star_bot",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bot_id")
    )
    private List<Bot> starBots;

    @OneToMany
    @JoinColumn(name = "create_bots")
    private List<Bot> createBots;



}
