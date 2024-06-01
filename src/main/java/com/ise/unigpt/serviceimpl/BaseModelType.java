package com.ise.unigpt.serviceimpl;


import com.fasterxml.jackson.annotation.JsonValue;

public enum BaseModelType {
    GPT(0, "gpt-3.5-turbo"),
    CLAUDE(1, "claude-instant-1.2"),
    LLAMA(2, "llama3-70b-8192"),
    KIMI(3, "moonshot-v1-8k");

    private final int value;
    private final String string;

    BaseModelType(int value, String string) {
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
