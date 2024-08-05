package com.ise.unigpt.model;

import java.util.ArrayList;
import java.util.List;

import com.ise.unigpt.dto.JaccountUserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "account")
    private String account;

    @Column(name = "canvas")
    private String canvasUrl;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @ManyToMany
    @JoinTable(name = "user_like_bot", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "bot_id"))
    private List<Bot> likeBots;

    @ManyToMany
    @JoinTable(name = "user_star_bot", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "bot_id"))
    private List<Bot> starBots;

    @ManyToMany
    @JoinTable(name = "user_used_bot", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "bot_id"))
    private List<Bot> usedBots;

    @OneToMany
    @JoinColumn(name = "creator_id")
    private List<Bot> createBots;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<History> histories;

    @Column(name = "is_admin")
    private Boolean asAdmin = false;

    @Column(name = "is_disabled")
    private Boolean disabled = false;

    @OneToMany
    @JoinColumn(name = "creator_id")
    private List<Plugin> createPlugins;


    public User(JaccountUserDTO dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
        this.account = dto.getAccount();
        this.asAdmin = false;
    }

    public User() {
        // not used
        this.likeBots = new ArrayList<>();
        this.starBots = new ArrayList<>();
        this.usedBots = new ArrayList<>();
        this.createBots = new ArrayList<>();

        this.histories = new ArrayList<>();
    }

}
