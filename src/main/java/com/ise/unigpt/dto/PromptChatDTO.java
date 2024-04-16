package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptChatType;
import lombok.Data;

@Data
public class PromptChatDTO {
    private PromptChatType type;
    private String content;
}
