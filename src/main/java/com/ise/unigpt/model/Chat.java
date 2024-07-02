package com.ise.unigpt.model;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat")
public class Chat implements ChatMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "history_id")
    private History history;

    @Column(name = "type")
    private ChatType type;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "is_visible")
    private Boolean isVisible;

    public Chat() {
        // not used
    }

    public Chat(History history, ChatType type, String content, Boolean isVisible) {
        this.history = history;
        this.type = type;
        this.content = content;
        this.isVisible = isVisible;
    }

    
    public Chat(History history, ChatMessage chatMessage) {
        this.history = history;
        this.type = chatMessage.type() == ChatMessageType.USER ? ChatType.USER : chatMessage.type() == ChatMessageType.AI ? ChatType.BOT : ChatType.SYSTEM;
        this.content = chatMessage.text();
        this.isVisible = true;
    }
    public Chat(ChatType type, String content) {
        // 只在测试使用
        this.history = null;
        this.type = type;
        this.content = content;
    }
    
    @Override
    public ChatMessageType type() {
        switch (type) {
            case USER:
                return ChatMessageType.USER;
            case BOT:
                return ChatMessageType.AI;
            case SYSTEM:
            default:
                return ChatMessageType.SYSTEM;
        }
    }

    @Override
    public String text() {
        return content;
    }
}
