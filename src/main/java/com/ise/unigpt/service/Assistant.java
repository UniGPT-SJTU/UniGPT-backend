package com.ise.unigpt.service;


import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;


public interface Assistant {
    Response<AiMessage> chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
