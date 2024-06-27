package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat")
public class Chat {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "history_id")
    private History history;

    @Column(name = "type")
    private ChatType type;

    @Column(name = "time")
    private Date time;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    public Chat() {
        // not used
    }

    public Chat(History history, ChatType type, String content) {
        this.history = history;
        this.type = type;
        this.time = new Date();
        this.content = content;
    }

    public Chat(ChatType type, String content) {
        // 只在测试使用
        this.history = null;
        this.type = type;
        this.content = content;
    }
}
