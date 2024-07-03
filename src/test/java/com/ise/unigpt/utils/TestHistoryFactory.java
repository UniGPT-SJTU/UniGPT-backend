package com.ise.unigpt.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.parameters.LLMArgs.LLMArgs;

public class TestHistoryFactory {
    static public History CreateHistory() throws Exception {
        History history;
        history = new History();
        history.setId(1);
        history.setBot(TestBotFactory.createBot());
        history.setUser(TestUserFactory.createUser());
        history.setPromptKeyValuePairs(Map.of("prompt1", "response1"));
        history.setChats(new ArrayList<>());
        history.setLastActiveTime(new Date());
        history.setLlmArgs(LLMArgs.builder().baseModelType(BaseModelType.GPT).temperature(0.5).build());

        ReflectionTestUtils.assertNoNullFields(history);
        return history;

    }
}
