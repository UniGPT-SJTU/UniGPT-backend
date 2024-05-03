package com.ise.unigpt.serviceimpl;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ise.unigpt.dto.OpenAIMessageDTO;
import com.ise.unigpt.dto.OpenAIRequestDTO;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.utils.StringTemplateParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

/**
 * 基于OpenAI接口的LLMService实现，
 * 需要配置环境变量： OPENAI_API_BASE_URL, OPENAI_API_KEY
 */
public class OpenAIService implements LLMService{

    @Override
    public String generateResponse(List<PromptChat> promptChats, Map<String, String> promptList, List<Chat> chats) throws Exception {
        Unirest.setTimeouts(0, 0);
        List<OpenAIMessageDTO> messages = new ArrayList<>();
        messages.addAll(
            promptChats.stream().map(
                promptChat -> new OpenAIMessageDTO(
                    promptChat.getType().toString(),
                    StringTemplateParser.interpolate(promptChat.getContent(), promptList)
                )
            ).collect(Collectors.toList()));
        messages.addAll(
            chats.stream().map(
                chat -> new OpenAIMessageDTO(chat.getType().toString(), chat.getContent())
            ).collect(Collectors.toList()));

        OpenAIRequestDTO dto = new OpenAIRequestDTO("gpt-3.5-turbo", messages);
        HttpResponse<String> response = Unirest.post(System.getenv("OPENAI_API_BASE_URL") + "/v1/chat/completions")
        .header("Accept", "application/json")
        .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
        .body(new Gson().toJson(dto)).asString();
        if(response.getStatus() != 200) {
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
