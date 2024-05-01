package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;



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

    @OneToMany
    private List<Chat> chats;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromptValue> promptValues;

    public History() {
        // not used
    }
    public History(User user, Bot bot, List<PromptValue> promptValues) {
        this.user = user;
        this.bot = bot;
        this.chats = new ArrayList<>();
        this.promptValues = promptValues;
    }

}
