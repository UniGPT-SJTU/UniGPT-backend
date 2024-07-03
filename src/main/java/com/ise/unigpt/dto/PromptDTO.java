package com.ise.unigpt.dto;
import lombok.Getter;

@Getter
public class PromptDTO {
    private final String promptKey;
    private final String promptValue;

    public PromptDTO(String promptKey, String promptValue) {
        this.promptKey = promptKey;
        this.promptValue = promptValue;
    }

    public String getKey() {
        return promptKey;
    }

    public String getValue() {
        return promptValue;
    }
}
