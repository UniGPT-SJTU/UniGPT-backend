package com.ise.unigpt.model;

import java.util.Date;
import java.util.List;

/**
 * 对话历史类
 */
public class ChatHistory {
    private final Integer id;
    private final Integer userId;
    private final Integer botId;
    private final String title;
    private final List<Integer> chats;


    /**
     * ChatHistory constructor
     * @param id
     * @param userId 用户id
     * @param botId 机器人id
     * @param title 对话历史标题
     * @param chats 所有对话记录id的列表
     */
    public ChatHistory(Integer id, Integer userId, Integer botId, String title, List<Integer> chats) {
        this.id = id;
        this.userId = userId;
        this.botId = botId;
        this.title = title;
        this.chats = chats;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getBotId() {
        return botId;
    }

    public String getTitle() {
        return title;
    }

    public List<Integer> getChats() {
        return chats;
    }
}
