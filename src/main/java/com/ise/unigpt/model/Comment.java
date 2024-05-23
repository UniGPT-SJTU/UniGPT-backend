package com.ise.unigpt.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

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

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    public Comment() {
    }

    public Comment(String content, Date time, User user, Bot bot) {
        this.content = content;
        this.time = time;
        this.user = user;
        this.bot = bot;
    }
}
