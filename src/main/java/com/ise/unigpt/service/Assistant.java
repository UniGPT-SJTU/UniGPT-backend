package com.ise.unigpt.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

interface Assistant {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
