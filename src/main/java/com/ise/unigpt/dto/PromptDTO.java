package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptValue;
import lombok.Getter;

@Getter
public class PromptDTO {
    private final String promptKey, promptValue;

    public PromptDTO(String promptKey, String promptValue) {
        this.promptKey = promptKey;
        this.promptValue = promptValue;
    }
}