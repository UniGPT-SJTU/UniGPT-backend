package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


/**
 * 对话历史类
 */
@Data
@Entity
@Table(name = "histories")
public class ChatHistory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "bot_id")
    private int botId;

    @Column(name = "title")
    private String title;

    @ElementCollection
    @CollectionTable(name = "chats", joinColumns = @JoinColumn(name = "chat"))
    private List<Integer> chats;
}
