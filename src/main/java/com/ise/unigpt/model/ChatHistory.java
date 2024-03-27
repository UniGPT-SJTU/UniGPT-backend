package com.ise.unigpt.model;

import java.util.Date;
import java.util.List;

/**
 * 对话历史
 * @param id
 * @param userId 用户id
 * @param botId 机器人id
 * @param title 对话历史的标题
 * @param chats 历史中对话记录的id列表
 */
public record ChatHistory(
        Integer id,
        Integer userId,
        Integer botId,
        String title,
        List<Integer> chats
) {
}
