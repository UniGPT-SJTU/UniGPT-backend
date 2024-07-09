package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.Assistant;
import com.ise.unigpt.service.DockerService;
import com.ise.unigpt.service.LLMService;

import static dev.langchain4j.agent.tool.JsonSchemaProperty.description;
import static dev.langchain4j.agent.tool.JsonSchemaProperty.type;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public class LLMServiceImpl implements LLMService {

    private String baseUrl;
    private String apiKey;
    private String modelName;

    private final ChatMemoryStore chatMemoryStore;

    private final DockerService dockerService;

    public LLMServiceImpl(BaseModelType type, ChatMemoryStore chatMemoryStore, DockerService dockerService) {
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
    }

    // TODO: 将preHandle移动到这里
    public void preHandle(int botId, List<PromptChat> promptChatList) {
        // if (botId == 22) {
        // String url = user.getCanvasUrl();
        // if (url == null || url.isEmpty()) {
        // System.out.println("Canvas URL is empty");
        // promptChatList.add(new PromptChat(PromptChatType.USER,
        // "我还没有在个人主页添加Canvas链接，请回答我“很抱歉，" +
        // "由于您还没有在个人主页添加Canvas链接，我无法帮助您规划任务。在您添加Canvas链接后，可以再次与我对话，我将很乐意帮助您规划任务安排。祝您顺利完成所有任务！”"));
        // return;
        // }
        // //
        // 正确的url格式:https://oc.sjtu.edu.cn/feeds/calendars/user_5ANNdRErwaHFWaUwCJuLqUk2kyoSNRwMGFtN933O.ics
        // // 假如url格式不是https://oc.sjtu.edu.cn/feeds/calendars/user_{一串字符}.ics，返回错误信息
        // if (!url.startsWith("https://oc.sjtu.edu.cn/feeds/calendars/user_") ||
        // !url.endsWith(".ics")) {
        // System.out.println("Canvas URL is invalid");
        // promptChatList.add(new PromptChat(PromptChatType.USER,
        // "我的个人主页的Canvas链接是错误的，请回答我“很抱歉，" +
        // "由于您在个人主页添加的Canvas链接是错误的，我无法帮助您规划任务。在您修改Canvas链接后，可以再次与我对话，我将很乐意帮助您规划任务安排。祝您顺利完成所有任务！”"));
        // return;
        // }
        // String canvasEventList = getCanvasEventList(url);
        // promptChatList
        // .add(new PromptChat(PromptChatType.USER, "Here are my upcoming Canvas
        // events:" + canvasEventList));
        // }
    }

    public TokenStream generateResponse(History history, String userMessage, GenerateResponseOptions options)
            throws Exception {

        // 获取history中的bot的plugins，将每个plugins创建对应的ToolSpecification
        // 通过ToolSpecification创建对应的ToolExecutor
        List<Plugin> plugins = history.getBot().getPlugins();
        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            // TODO: notify the frontend that a tool is being executed
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

            String output = dockerService.invokeFunction(toolExecutionRequest.name(), "handler", valuesList);
            options.getSendFunctionResult().accept(options.getSession(), output);
            // TODO: notify the frontend that the tool has been executed
            System.out.println("Tool output: " + output);
            return output;
        };
        for (Plugin plugin : plugins) {
            ToolSpecification.Builder toolSpecificationBuilder = ToolSpecification.builder()
                    .name(plugin.getName())
                    .description(plugin.getDescription());
            for (int i = 0; i < plugin.getParameters().size(); i++) {
                toolSpecificationBuilder.addParameter(plugin.getParameters().get(i).getName(), type(plugin.getParameters().get(i).getType()), description(plugin.getParameters().get(i).getDescription()));
            }
            ToolSpecification toolSpecification = toolSpecificationBuilder.build();

            tools.put(toolSpecification, toolExecutor);
        }

        // // TODO: 集成prehandle函数
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl + "/v1")
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(history.getLlmArgs().getAdjustedTemperature())
                .build();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                // .chatMemoryStore(chatMemoryStore)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(tools)
                .build();

        return assistant.chat(history.getId(), userMessage);
    }
}
