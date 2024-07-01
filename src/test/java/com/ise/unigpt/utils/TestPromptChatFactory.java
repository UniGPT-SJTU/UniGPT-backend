package com.ise.unigpt.utils;

import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;

public class TestPromptChatFactory {
    public static PromptChat createUserPromptChat() throws Exception {
        PromptChat promptChat = new PromptChat();
        promptChat.setId(1);
        promptChat.setType(ChatType.USER);
        promptChat.setContent("user prompt chat");

        ReflectionTestUtils.assertNoNullFields(promptChat);
        return promptChat;
    }

    public static PromptChat createBotPromptChat() throws Exception {
        PromptChat promptChat = new PromptChat();
        promptChat.setId(2);
        promptChat.setType(ChatType.BOT);
        promptChat.setContent("bot prompt chat");

        ReflectionTestUtils.assertNoNullFields(promptChat);
        return promptChat;
    }
}
