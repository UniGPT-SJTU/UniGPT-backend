package com.ise.unigpt.model;


import com.fasterxml.jackson.annotation.JsonValue;

public enum PromptChatType {
    USER(0, "user"),
    ASSISTANT(1, "assistant"),
    SYSTEM(2, "system");

    private final int value;
    private final String string;

    PromptChatType(int value, String string) {
        this.value = value;
        this.string = string;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
    public String toString() {
        return string;
    }
}
