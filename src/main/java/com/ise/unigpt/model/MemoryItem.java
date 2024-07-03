package com.ise.unigpt.model;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "memory_item")
public class MemoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private ChatMessageType type;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "memory_id")
    private Memory memory;

    public MemoryItem() {
        // not used
    }

    public MemoryItem(Chat chat, Memory memory) {
        switch (chat.getType()) {
            case USER:
                type = ChatMessageType.USER;
                break;
            case BOT:
                type = ChatMessageType.AI;
                break;
            case SYSTEM:
            default:
                type = ChatMessageType.SYSTEM;
                break;

        }
        this.content = chat.getContent();
        this.memory = memory;
    }

    public MemoryItem(ChatMessage chatMessage, Memory memory) {
        this.type = chatMessage.type();
        this.content = chatMessage.text();
        this.memory = memory;
    }

    public ChatMessage toChatMessage() {
        switch (type) {
            case USER:
                return new UserMessage(content);
            case AI:
                return new AiMessage(content);
            case SYSTEM:
            default:
                return new SystemMessage(content);
        }
    }
}
