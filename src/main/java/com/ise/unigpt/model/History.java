package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
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

    @OneToMany
    private List<PromptValue> promptValues;
}
