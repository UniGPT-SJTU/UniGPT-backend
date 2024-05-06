package com.ise.unigpt.websocket;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.PromptChatType;
import com.ise.unigpt.service.LLMService;
import com.ise.unigpt.serviceimpl.OpenAIService;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final LLMService llmService;

    public ChatWebSocketHandler() {
        this.llmService = new OpenAIService();
    }

    /*
     * @Override
     * public void afterConnectionEstablished(WebSocketSession session) throws
     * Exception {
     * // 从 WebSocketSession 中获取 HttpHeaders
     * HttpHeaders headers = session.getHandshakeHeaders();
     * 
     * // 从 HttpHeaders 中获取 Cookie
     * String cookieHeader = headers.getFirst(HttpHeaders.COOKIE);
     * if (cookieHeader != null) {
     * Cookie[] cookies = parseCookies(cookieHeader);
     * 
     * // 查找身份验证 Cookie
     * if (authCookie != null) {
     * String authToken = authCookie.getValue();
     * 
     * // 验证用户身份
     * boolean isAuthenticated = verifyUser(authToken);
     * if (!isAuthenticated) {
     * // 身份验证失败，关闭连接
     * session.close(CloseStatus.FORBIDDEN.getReasonPhrase());
     * return;
     * }
     * } else {
     * // 未找到身份验证 Cookie，关闭连接
     * session.close(CloseStatus.FORBIDDEN.getReasonPhrase());
     * session.close(CloseStatus.FORBIDDEN);
     * return;
     * }
     * } else {
     * // 未找到 Cookie，关闭连接
     * session.close(CloseStatus.FORBIDDEN);
     * return;
     * }
     * 
     * // 身份验证成功，继续处理 WebSocket 消息
     * super.afterConnectionEstablished(session);
     * }
     * 
     * private Cookie[] parseCookies(String cookieHeader) {
     * // 将 Cookie 头解析为 Cookie 对象数组
     * return Arrays.stream(cookieHeader.split(";"))
     * .map(String::trim)
     * .map(cookie -> {
     * String[] parts = cookie.split("=");
     * return new Cookie(parts[0], parts[1]);
     * })
     * .toArray(Cookie[]::new);
     * }
     * 
     * private Cookie findCookie(Cookie[] cookies, String name) {
     * // 查找特定名称的 Cookie
     * return Arrays.stream(cookies)
     * .filter(cookie -> cookie.getName().equals(name))
     * .findFirst()
     * .orElse(null);
     * }
     * 
     * private boolean verifyUser(String authToken) {
     * // 验证用户身份的逻辑
     * // 例如，可以检查身份验证令牌是否有效
     * // 返回 true 表示验证成功，返回 false 表示验证失败
     * return true; // 示例中返回 true，请根据实际需求实现验证逻辑
     * }
     * 
     * // 其他处理 WebSocket 消息的方法...
     */
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        // 获取消息的payload（body）
        String payload = message.getPayload();

        // 现在你可以根据需要处理payload
        System.out.println("Received message: " + payload);
        // TODO: 将payload转化成json对应的DTO对象
        // 目前先用testChat()方法的返回值作为回复
        String response = testChat();
        System.out.println("Sending response: " + response);
        try {
            // 发送回复消息
            session.sendMessage(new TextMessage(response));
            session.close(CloseStatus.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String testChat() {
        List<PromptChat> promptChatList = new ArrayList<>();
        // 机器人创建时的模板对话
        promptChatList.add(new PromptChat(PromptChatType.SYSTEM, "你是一个++{lang}大师，精通++{lang}的所有语法和库。"));
        promptChatList.add(new PromptChat(PromptChatType.USER, "如何使用++{lang}++{question}？"));

        // 历史在创建时，用户填入的提示词键值对
        Map<String, String> promptList = Map.of(
                "lang", "java",
                "question", "计算1-100的和");

        // 用户创建历史之后，和机器人的对话
        List<Chat> chatList = new ArrayList<>();
        // 机器人回答
        chatList.add(new Chat(ChatType.BOT,
                "你可以使用一个简单的for循环来计算1到100这一范围的所有数字的和。以下是一个示例代码： ```java public class CalculateSum { public static void main(String[] args) { int sum = 0; for (int i = 1; i <= 100; i++) { sum += i; } System.out.println(\"1到100的和为：\" + sum); } } ``` 在这个示例中，我们初始化一个变量`sum`为0，然后使用for循环从1累加到100，最后打印出1到100的和。你可以运行这段代码来得到结果。"));
        // 用户追问
        chatList.add(new Chat(ChatType.USER, "有没有更加简洁的求和方式？"));
        try {
            return llmService.generateResponse(promptChatList, promptList, chatList);
        } catch (Exception e) {
            return "error met";
        }
    }
}