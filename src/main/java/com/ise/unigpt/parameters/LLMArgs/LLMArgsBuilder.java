package com.ise.unigpt.parameters.LLMArgs;

import lombok.Data;

@Data
public class LLMArgsBuilder {
    private Double temperature;

    LLMArgsBuilder() {
        
    }
    
    public LLMArgsBuilder temperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public LLMArgs build() {
        LLMArgs args = new LLMArgs();
        args.setTemperature(this.temperature);
        return args;
    }
}
