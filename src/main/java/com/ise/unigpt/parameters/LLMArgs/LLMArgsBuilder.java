package com.ise.unigpt.parameters.LLMArgs;

import com.ise.unigpt.model.BaseModelType;

import lombok.Data;

@Data
public class LLMArgsBuilder {
    private BaseModelType baseModelType;
    private Double temperature;

    LLMArgsBuilder() {
        
    }
    
    public LLMArgsBuilder baseModelType(BaseModelType baseModelType) {
        this.baseModelType = baseModelType;
        return this;
    }
    public LLMArgsBuilder temperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public LLMArgs build() {
        LLMArgs args = new LLMArgs();
        args.setBaseModelType(baseModelType);
        args.setTemperature(this.temperature);
        return args;
    }
}
