package com.ise.unigpt.serviceimpl;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ise.unigpt.dto.OpenAIMessageDTO;
import com.ise.unigpt.dto.OpenAIRequestDTO;
import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.LLMService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class LLMServiceImpl implements LLMService{

    private String BASE_URL;
    private String API_KEY;
    private String MODEL_NAME;

    public LLMServiceImpl(BaseModelType type) {
        switch (type) {
            case CLAUDE:
                BASE_URL = System.getenv("CLAUDE_API_BASE_URL");
                API_KEY = System.getenv("CLAUDE_API_KEY");
                MODEL_NAME = "claude-instant-1.2";
                break;
            case LLAMA:
                BASE_URL = System.getenv("LLAMA_API_BASE_URL");
                API_KEY = System.getenv("LLAMA_API_KEY");
                MODEL_NAME = "llama3-70b-8192";
                break;
            case KIMI:
                BASE_URL = System.getenv("KIMI_API_BASE_URL");
                API_KEY = System.getenv("KIMI_API_KEY");
                MODEL_NAME = "moonshot-v1-8k";
                break;
            default:
                BASE_URL = System.getenv("OPENAI_API_BASE_URL");
                API_KEY = System.getenv("OPENAI_API_KEY");
                MODEL_NAME = "gpt-3.5-turbo";
                break;
        }
    }

    public String generateResponse(List<PromptChat> promptChats, List<Chat> chats)
            throws Exception {
        Unirest.setTimeouts(0, 0);
        List<OpenAIMessageDTO> messages = new ArrayList<>();
        messages.addAll(
                promptChats
                        .stream()
                        .map(promptChat -> new OpenAIMessageDTO(promptChat.getType().toString(),
                                promptChat.getContent()))
                        .toList());
        messages.addAll(
                chats.stream().map(
                                chat -> new OpenAIMessageDTO(chat.getType().toString(),
                                        chat.getContent()))
                        .collect(Collectors.toList()));

        OpenAIRequestDTO dto = new OpenAIRequestDTO(MODEL_NAME, messages);
        HttpResponse<String> response = Unirest
                .post(BASE_URL + "/v1/chat/completions")
                .header("Accept", "application/json")
                .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .body(new Gson().toJson(dto)).asString();
        if (response.getStatus() != 200) {
            throw new ServerException("openai service error");
        }
        JsonObject responseJsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
        String responseContent = responseJsonObject
                .get("choices").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();

        return responseContent;
    }
}
