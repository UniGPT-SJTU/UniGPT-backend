package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.service.Assistant;
import com.ise.unigpt.service.DockerService;
import com.ise.unigpt.service.KnowledgeService;
import com.ise.unigpt.service.LLMService;

import static dev.langchain4j.agent.tool.JsonSchemaProperty.description;
import static dev.langchain4j.agent.tool.JsonSchemaProperty.type;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public class LLMServiceImpl implements LLMService {

    private String baseUrl;
    private String apiKey;
    private String modelName;

    private final ChatMemoryStore chatMemoryStore;

    private final DockerService dockerService;

    private final KnowledgeService knowledgeService;

    public LLMServiceImpl(
            BaseModelType type,
            ChatMemoryStore chatMemoryStore,
            DockerService dockerService,
            KnowledgeService knowledgeService) {
        switch (type) {
            case CLAUDE:
                baseUrl = System.getenv("CLAUDE_API_BASE_URL");
                apiKey = System.getenv("CLAUDE_API_KEY");
                modelName = "claude-instant-1.2";
                break;
            case LLAMA:
                baseUrl = System.getenv("LLAMA_API_BASE_URL");
                apiKey = System.getenv("LLAMA_API_KEY");
                modelName = "llama3-70b-8192";
                break;
            case KIMI:
                baseUrl = System.getenv("KIMI_API_BASE_URL");
                apiKey = System.getenv("KIMI_API_KEY");
                modelName = "moonshot-v1-8k";
                break;
            default:
                baseUrl = System.getenv("OPENAI_API_BASE_URL");
                apiKey = System.getenv("OPENAI_API_KEY");
                modelName = "gpt-3.5-turbo";
                break;
        }
        this.chatMemoryStore = chatMemoryStore;
        this.dockerService = dockerService;
        this.knowledgeService = knowledgeService;
    }

    @Override
    public TokenStream generateResponse(History history, String userMessage, GenerateResponseOptions options)
            throws Exception {

        // 获取history中的bot的plugins，将每个plugins创建对应的ToolSpecification
        // 通过ToolSpecification创建对应的ToolExecutor
        List<Plugin> plugins = history.getBot().getPlugins();
        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            System.out.println("Executing tool: " + toolExecutionRequest.name());
            options.getSendFunctionCall().accept(options.getSession(), toolExecutionRequest.name());
            String argument = toolExecutionRequest.arguments();

            // Parse the argument JSON string to a JSONObject
            JSONObject jsonArgument = new JSONObject(argument);
            List<String> valuesList = new ArrayList<>();

            // Iterate over all keys and add their values to the list
            jsonArgument.keys().forEachRemaining(key -> {
                valuesList.add(jsonArgument.get(key).toString());
            });

            // username 为toolname第一个“_”之前的字符串
            String toolName = toolExecutionRequest.name();
            String toolUsername = toolName.substring(0, toolName.indexOf("_"));
            // toolname去掉username
            String toolNameModified = toolName.substring(toolName.indexOf("_") + 1);
            String output = dockerService.invokeFunction(toolUsername, toolNameModified, "handler", valuesList);
            options.getSendFunctionResult().accept(options.getSession(), output);

            System.out.println("Tool output: " + output);
            return output;
        };
        for (Plugin plugin : plugins) {
            String toolName = plugin.getCreator().getAccount() + "_" + plugin.getName();
            ToolSpecification.Builder toolSpecificationBuilder = ToolSpecification.builder()
                    .name(toolName)
                    .description(plugin.getDescription());
            for (int i = 0; i < plugin.getParameters().size(); i++) {
                toolSpecificationBuilder.addParameter(plugin.getParameters().get(i).getName(),
                        type(plugin.getParameters().get(i).getType()),
                        description(plugin.getParameters().get(i).getDescription()));
            }
            ToolSpecification toolSpecification = toolSpecificationBuilder.build();

            tools.put(toolSpecification, toolExecutor);
        }

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl + "/v1")
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(history.getLlmArgs().getAdjustedTemperature())
                .build();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(chatMemoryStore)
                .build();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(
                        knowledgeService.createEmbeddingStore(history.getBot().getId()))
                .embeddingModel(new AllMiniLmL6V2EmbeddingModel())
                .maxResults(2) // on each interaction we will retrieve the 2 most relevant segments
                .minScore(0.5) // we want to retrieve segments at least somewhat similar to user query
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(tools)
                .contentRetriever(contentRetriever)
                .build();

        options.getSendFunctionCall().accept(options.getSession(), "知识库调用");
        options.getSendFunctionResult().accept(options.getSession(), "知识库调用结果");
        return assistant.chat(history.getId(), userMessage);
    }
}
