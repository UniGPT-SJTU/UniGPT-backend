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
}
