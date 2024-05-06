package com.ise.unigpt.websocket;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ChatWebSocketHandler extends TextWebSocketHandler {
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
    }
}
