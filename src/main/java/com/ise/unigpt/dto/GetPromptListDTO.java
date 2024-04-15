package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptValue;

import java.util.List;

public class GetPromptListDTO {
    private List<String> promptList;

    public GetPromptListDTO(List<PromptValue> promptList) {
        for (PromptValue promptValue : promptList) {
            this.promptList.add(promptValue.getContent());
        }
    }

    public List<String> getPromptList() {
        return promptList;
    }
}
