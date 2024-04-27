package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetPromptListDTO {
    private final List<String> promptList;

    public GetPromptListDTO(List<PromptValue> promptList) {
        this.promptList = promptList.stream().map(PromptValue::getContent).collect(Collectors.toList());
    }
}
