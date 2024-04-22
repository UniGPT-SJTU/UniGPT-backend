package com.ise.unigpt.model;

import com.ise.unigpt.dto.JaccountUserDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @ManyToMany
    @JoinTable(
            name = "user_used_bot",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bot_id")
    )
    private List<Bot> usedBots;

    @OneToMany
    private List<Bot> createBots;

    @OneToMany
    private List<History> histories;

    public User(RegisterRequestDTO dto) {

        this.name = dto.getUsername();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();

        this.likeBots = new ArrayList<>();
        this.starBots = new ArrayList<>();
        this.usedBots = new ArrayList<>();
        this.createBots = new ArrayList<>();
    }

    public User(JaccountUserDTO dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.avatar = dto.getAvatar();
        this.description = dto.getDescription();
    }
    
    public User() {
        // not used
    }
}
