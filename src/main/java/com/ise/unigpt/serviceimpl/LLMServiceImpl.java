package com.ise.unigpt.serviceimpl;

import java.util.List;

import com.ise.unigpt.model.BaseModelType;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.service.Assistant;
import com.ise.unigpt.service.LLMService;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import okhttp3.internal.ws.RealWebSocket.Message;

public class LLMServiceImpl implements LLMService {

    private String baseUrl;
    private String apiKey;
    private String modelName;

    private final ChatMemoryStore chatMemoryStore;


    public LLMServiceImpl(BaseModelType type, ChatMemoryStore chatMemoryStore) {
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

    public String generateResponse(History history, String userMessage, GenerateResponseOptions options)
            throws Exception {
        // // TODO: 集成prehandle函数
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl + "/v1")
                .apiKey(apiKey)
                .modelName(modelName)
                .build();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .build();


        return assistant.chat(history.getId(), userMessage);
        // Unirest.setTimeouts(0, 0);
        // List<Chat>chats = history.getChats();
        // if(options.getCover()) {
        // chats = chats.size() < 2 ? new ArrayList<>() : chats.subList(0, chats.size()
        // - 2);
        // }
        // List<OpenAIMessageDTO> messages = new ArrayList<>();
        // messages.addAll(
        // chats.stream().map(
        // chat -> new OpenAIMessageDTO(chat.getType().toString(),
        // chat.getContent()))
        // .collect(Collectors.toList()));

        // if(!options.getIsUserAsk()) {
        // messages.add(new OpenAIMessageDTO(ChatType.USER.toString(), userMessage));
        // }

        // OpenAIRequestDTO dto = new OpenAIRequestDTO(modelName, messages,
        // history.getLlmArgs().getTemperature());
        // HttpResponse<String> response;
        // int retryCount = 0;
        // while (true){
        // response = Unirest
        // .post(baseUrl + "/v1/chat/completions")
        // .header("Accept", "application/json")
        // .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
        // .header("Content-Type", "application/json")
        // .header("Authorization", "Bearer " + apiKey)
        // .body(new Gson().toJson(dto)).asString();
        // if (response.getStatus() == 429) {
        // if (retryCount > 3) {
        // break;
        // }
        // System.out.println("retry after 5 seconds");
        // retryCount++;
        // Thread.sleep(5000);
        // } else {
        // break;
        // }
        // }
        // if (response.getStatus() != 200) {
        // System.out.println("status code: " + response.getStatus());
        // System.out.println("response body: " + response.getBody());
        // throw new ServerException("openai service error");
        // }
        // JsonObject responseJsonObject =
        // JsonParser.parseString(response.getBody()).getAsJsonObject();
        // String responseContent = responseJsonObject
        // .get("choices").getAsJsonArray()
        // .get(0).getAsJsonObject()
        // .get("message").getAsJsonObject()
        // .get("content").getAsString();
        // return responseContent;
    }
}
