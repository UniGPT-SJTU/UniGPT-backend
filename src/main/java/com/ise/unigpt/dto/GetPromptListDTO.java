package com.ise.unigpt.dto;

import com.ise.unigpt.model.PromptValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//get prompt response example:
//[
//    {
//        "promptKey": "programing language",
//        "promptValue": "python"
//    },
//    {
//        "promptKey": "code",
//        "promptValue": "def hello:\n\t print(\"hello!\")"
//    },
//    {
//        "promptKey": "bug",
//        "promptValue": "程序没有输出"
//    }
//]

@Getter
public class GetPromptListDTO {
    private final List<PromptDTO> promptList;
    public GetPromptListDTO(List<String> promptKeys, List<PromptValue> promptValues) {
        int size = promptKeys.size();
        promptList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if(promptValues.get(i) == null) {
                promptList.add(new PromptDTO(promptKeys.get(i), ""));
            } else {
                promptList.add(new PromptDTO(promptKeys.get(i), promptValues.get(i).getContent()));
            }
        }
    }
}
