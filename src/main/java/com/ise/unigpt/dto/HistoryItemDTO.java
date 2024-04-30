package com.ise.unigpt.dto;

import com.ise.unigpt.model.History;
import lombok.Data;

import com.ise.unigpt.model.Chat;

/**
 * 对话历史的DTO，在接口返回对话历史时使用
 */
@Data
public class HistoryItemDTO {
    int id;
    String title;
    String content;

    public HistoryItemDTO() {
        // not used
    }

    public HistoryItemDTO(History history) {
        this.id = history.getId();
        this.title = "title"; // title需要GPT根据history的内容生成
        // 最新的一条对话
        if(!history.getChats().isEmpty()) {
            Chat chat = history.getChats().get(history.getChats().size() - 1);
            this.content = chat.getContent();
        } else {
            this.content = "";
        }
    }
}
