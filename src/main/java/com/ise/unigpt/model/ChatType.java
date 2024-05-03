package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatType {
    USER(0, "user"),
    BOT(1, "assistant");

    private final int value;
    private final String string;

    ChatType(int value, String string) {
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
