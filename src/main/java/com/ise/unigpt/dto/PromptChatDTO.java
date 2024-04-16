package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import lombok.Data;

@Data
public class PromptChatDTO {
    private PromptChatType type;
    private String content;

    // TODO: 需要审查
    public PromptChatDTO(PromptChat promptChat){
        this.type = promptChat.getType();
        this.content = promptChat.getContent();
    }
    public PromptChatDTO() {
        // not used
    }

}
