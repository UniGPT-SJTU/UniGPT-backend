package com.ise.unigpt.dto;

import com.ise.unigpt.model.ChatType;
import lombok.Data;

@Data
public class PromptChatDTO {
    ChatType type;
    String content;
}
