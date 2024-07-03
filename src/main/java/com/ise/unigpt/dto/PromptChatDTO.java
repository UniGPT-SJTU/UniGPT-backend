package com.ise.unigpt.dto;

import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;
import lombok.Data;

@Data
public class PromptChatDTO {
    private ChatType type;
    private String content;

    public PromptChatDTO(PromptChat promptChat){
        this.type = promptChat.getType();
        this.content = promptChat.getContent();
    }
    public PromptChatDTO() {
        // not used
    }

}
