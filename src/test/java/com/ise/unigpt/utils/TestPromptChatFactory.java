package com.ise.unigpt.utils;

import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;

public class TestPromptChatFactory {
    public static PromptChat createUserPromptChat() {
        PromptChat promptChat = new PromptChat();
        promptChat.setId(1);
        promptChat.setType(PromptChatType.USER);
        promptChat.setContent("user prompt chat");
        return promptChat;
    }

    public static PromptChat createBotPromptChat() {
        PromptChat promptChat = new PromptChat();
        promptChat.setId(2);
        promptChat.setType(PromptChatType.ASSISTANT);
        promptChat.setContent("bot prompt chat");
        return promptChat;
    }
}
