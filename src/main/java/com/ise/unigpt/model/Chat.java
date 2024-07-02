package com.ise.unigpt.model;

import java.util.List;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.rag.content.Content;
import jakarta.persistence.*;
import lombok.Data;

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
        switch (chatMessage.type()) {
            case USER:
                this.type = ChatType.USER;
                break;
            case AI: 
                this.type = ChatType.BOT;
                break;
            case SYSTEM:
            default:
                this.type = ChatType.SYSTEM;
                break;
        }
        this.content = chatMessage.text();
        this.isVisible = true;
    }

    public Chat(ChatType type, String content) {
        // 只在测试使用
        this.history = null;
        this.type = type;
        this.content = content;
    }

    public ChatMessage toChatMessage() {
        switch (type) {
            case USER:
                return new UserMessage(content);
            case BOT:
                return new AiMessage(content);
            case SYSTEM:
            default:
                return new SystemMessage(content);
        }
    }
}
