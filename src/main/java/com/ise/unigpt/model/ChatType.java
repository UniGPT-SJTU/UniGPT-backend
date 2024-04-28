package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatType {
    USER(0),
    BOT(1);

    private final int value;

    ChatType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
