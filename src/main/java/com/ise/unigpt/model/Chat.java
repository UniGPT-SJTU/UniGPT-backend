package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 对话记录类
 */
@Data
@Entity
@Table(name = "chats")
public class Chat {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "history_id")
    int historyId;

    @Column(name = "type")
    ChatType type;

    @Column(name = "content")
    String content;
}
