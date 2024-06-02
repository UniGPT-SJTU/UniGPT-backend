package com.ise.unigpt.model;


import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum BaseModelType {
    GPT(0, "gpt-3.5-turbo"),
    CLAUDE(1, "claude-instant-1.2"),
    LLAMA(2, "llama3-70b-8192"),
    KIMI(3, "moonshot-v1-8k");

    private final int value;
    private final String string;

    // 用于缓存 int 到 BaseModelType 的映射关系
    private static final Map<Integer, BaseModelType> valueToEnumMap = new HashMap<>();

    // 静态块初始化映射关系
    static {
        for (BaseModelType type : BaseModelType.values()) {
            valueToEnumMap.put(type.getValue(), type);
        }
    }


    BaseModelType(int value, String string) {
        this.value = value;
        this.string = string;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return string;
    }

    // 静态方法实现 int 到 BaseModelType 的转换
    public static BaseModelType fromValue(int value) {
        BaseModelType type = valueToEnumMap.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
        return type;
    }
}
