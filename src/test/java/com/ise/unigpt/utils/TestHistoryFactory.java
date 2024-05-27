package com.ise.unigpt.utils;

import java.util.ArrayList;
import java.util.Map;

import com.ise.unigpt.model.History;

public class TestHistoryFactory {
    static public History CreateHistory() {
        History history;
        history = new History();
        history.setId(1);
        history.setBot(TestBotFactory.createBot());
        history.setUser(TestUserFactory.createUser());
        history.setPromptList(Map.of("prompt1", "response1"));
        history.setChats(new ArrayList<>());
        return history;

    }
}
