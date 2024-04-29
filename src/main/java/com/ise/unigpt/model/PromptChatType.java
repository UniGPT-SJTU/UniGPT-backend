package com.ise.unigpt.model;


import com.fasterxml.jackson.annotation.JsonValue;

public enum PromptChatType {
    USER(0),
    ASSISTANT(1),
    SYSTEM(2);

    private final int value;

    PromptChatType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
