package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "history")
public class History {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Chat> chats;

    // // TODO: 使用java.utils.Map存储
    // @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<PromptValue> promptValues;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> promptList;

    public History() {
        // not used
    }

    public History(User user, Bot bot, Map<String, String> promptList) {
        this.user = user;
        this.bot = bot;
        this.chats = new ArrayList<>();
        this.promptList = promptList;
    }

}
