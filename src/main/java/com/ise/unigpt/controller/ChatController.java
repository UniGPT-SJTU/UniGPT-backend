package com.ise.unigpt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;

/**
 * 这是一个LLMService接口的使用样例，供后端WebSocket开发者参考，后续会从项目中删除。
 */
@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private LLMService llmService;
    public ChatController() {
        llmService = new OpenAIService();
    }

    @GetMapping
    String testChat() {
        List<PromptChat> promptChatList = new ArrayList<>();
        // 机器人创建时的模板对话
        promptChatList.add(new PromptChat(PromptChatType.SYSTEM, "你是一个++{lang}大师，精通++{lang}的所有语法和库。"));
        promptChatList.add(new PromptChat(PromptChatType.USER, "如何使用++{lang}++{question}？"));

        // 历史在创建时，用户填入的提示词键值对
        Map<String, String> promptList = Map.of(
            "lang", "java",
            "question", "计算1-100的和"
        );

        // 用户创建历史之后，和机器人的对话
        List<Chat> chatList = new ArrayList<>();
            // 机器人回答
        chatList.add(new Chat(ChatType.BOT, "你可以使用一个简单的for循环来计算1到100这一范围的所有数字的和。以下是一个示例代码： ```java public class CalculateSum { public static void main(String[] args) { int sum = 0; for (int i = 1; i <= 100; i++) { sum += i; } System.out.println(\"1到100的和为：\" + sum); } } ``` 在这个示例中，我们初始化一个变量`sum`为0，然后使用for循环从1累加到100，最后打印出1到100的和。你可以运行这段代码来得到结果。"));
            // 用户追问
        chatList.add(new Chat(ChatType.USER, "有没有更加简洁的求和方式？"));
        try {
            return llmService.generateResponse(promptChatList, promptList, chatList);
        } catch(Exception e) {
            return "error met";
        }
    }
}
