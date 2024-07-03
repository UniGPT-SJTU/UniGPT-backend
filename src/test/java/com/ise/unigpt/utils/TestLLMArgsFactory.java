package com.ise.unigpt.utils;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.parameters.LLMArgs.LLMArgs;

public class TestLLMArgsFactory {
    public static LLMArgs createLLMArgs() {
        return LLMArgs.builder().baseModelType(BaseModelType.GPT).temperature(0.5).build();
    }
}
