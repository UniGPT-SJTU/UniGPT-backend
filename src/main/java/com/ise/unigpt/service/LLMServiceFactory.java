package com.ise.unigpt.service;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.serviceimpl.LLMServiceImpl;

import dev.langchain4j.store.memory.chat.ChatMemoryStore;

@Component
public class LLMServiceFactory {

    private final Map<BaseModelType, LLMService> llmServiceMap;
    

    public LLMServiceFactory(ChatMemoryStore chatMemoryStore) {
        llmServiceMap = new HashMap<>();
        for(BaseModelType type: BaseModelType.values()) {
            llmServiceMap.put(type, new LLMServiceImpl(type, chatMemoryStore));
        }
    }

    public LLMService getLLMService(BaseModelType baseModelType) {
        return llmServiceMap.get(baseModelType);
    }
}
