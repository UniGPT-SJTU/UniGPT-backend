package com.ise.unigpt.model;

import com.ise.unigpt.dto.PromptChatDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prompt_chat")
public class PromptChat {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "type")
    PromptChatType type;

    @Column(name = "content") // TODO: length
    String content;

    public PromptChat(PromptChatDTO dto) {
        this.type = dto.getType();
        this.content = dto.getContent();
    }
    public PromptChat(PromptChatType type, String content) {
        this.type = type;
        this.content = content;
    }
    public PromptChat() {
        // not used
    }
}
