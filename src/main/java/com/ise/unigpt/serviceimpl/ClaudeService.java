package com.ise.unigpt.serviceimpl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ise.unigpt.dto.OpenAIMessageDTO;
import com.ise.unigpt.dto.OpenAIRequestDTO;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.LLMService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 考虑与OpenAIService合并
public class ClaudeService implements LLMService {

    @Override
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

        OpenAIRequestDTO dto = new OpenAIRequestDTO("claude-instant-1.2", messages);
        HttpResponse<String> response = Unirest
                .post(System.getenv("CLAUDE_API_BASE_URL") + "/v1/chat/completions")
                .header("Accept", "application/json")
                .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("CLAUDE_API_KEY"))
                .body(new Gson().toJson(dto)).asString();
        if (response.getStatus() != 200) {
            throw new ServerException("claude service error");
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