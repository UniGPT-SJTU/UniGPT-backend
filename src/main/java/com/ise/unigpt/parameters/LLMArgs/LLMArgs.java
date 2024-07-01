package com.ise.unigpt.parameters.LLMArgs;

import com.ise.unigpt.model.BaseModelType;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class LLMArgs {
    private BaseModelType baseModelType;
    private Double temperature;

    LLMArgs() {
        
    }
    public static LLMArgsBuilder builder() {
        return new LLMArgsBuilder();
    }
}
