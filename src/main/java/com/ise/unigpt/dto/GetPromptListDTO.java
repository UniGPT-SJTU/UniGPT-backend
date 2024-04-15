package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptItem;

import java.util.List;

public class GetPromptListDTO {
    private List<String> promptList;

    public GetPromptListDTO(List<PromptItem> promptList) {
        for (PromptItem promptItem : promptList) {
            this.promptList.add(promptItem.getContent());
        }
    }

    public List<String> getPromptList() {
        return promptList;
    }
}
