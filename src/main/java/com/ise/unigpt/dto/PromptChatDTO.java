package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import lombok.Data;

@Data
public class PromptChatDTO {
    private PromptChatType type;
    private String content;

    public PromptChatDTO(PromptChat promptChat){
        this.type = promptChat.getType();
        this.content = promptChat.getContent();
    }
}
