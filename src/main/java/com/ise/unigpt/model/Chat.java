package com.ise.unigpt.model;

/**
 * 对话记录类
 */
public class Chat {
    private final Integer id;
    private final Integer historyId;
    private final ChatType type;
    private final String content;

    /**
     * Chat constructor
     * @param id
     * @param historyId 对话记录所在历史的id
     * @param type 对话记录的类型(USER or BOT)
     * @param content 对话记录的内容
     */
    public Chat(Integer id, Integer historyId, ChatType type, String content) {
        this.id = id;
        this.historyId = historyId;
        this.type = type;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public String getContent() {
        return content;
    }

    public ChatType getType() {
        return type;
    }
}
