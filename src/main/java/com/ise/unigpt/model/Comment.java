package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;


/**
 * @brief 评论的类
 */
@Data
@Entity
@Table(name = "comment")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "time")
    private String time;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    public Comment() {
    }

    public Comment(String content, String time, User user, Bot bot) {
        this.content = content;
        this.time = time;
        this.user = user;
        this.bot = bot;
    }
}
