package com.ise.unigpt.model;

/**
 * 一条对话记录
 * @param id
 * @param historyId 对话记录所在历史的id
 * @param type 对话记录的类型(USER or BOT)
 * @param content 对话记录的内容
 */
public record Chat(
        Integer id,
        Integer historyId,
        ChatType type,
        String content
) {
}
