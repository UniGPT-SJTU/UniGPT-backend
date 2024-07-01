package com.ise.unigpt.parameters.LLMArgs;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class LLMArgs {
    private Double temperature;

    LLMArgs() {
        
    }

    public static LLMArgsBuilder builder() {
        return new LLMArgsBuilder();
    }
}
