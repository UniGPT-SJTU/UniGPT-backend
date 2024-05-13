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
        // 最新的一条对话
        if(!history.getChats().isEmpty()) {
            Chat chat = history.getChats().get(history.getChats().size() - 1);
            // content 为最新一句话的句话的前20个字符，如果超出则增加省略号
            this.content = chat.getContent().length() > 20 ? (chat.getContent().substring(0, 20) + "..."): chat.getContent();
            chat = history.getChats().get(0);
            // title 为第一句话的前10个字符，如果超出则增加省略号
            this.title = chat.getContent().length() > 10 ? (chat.getContent().substring(0, 10) + "..."): chat.getContent();

            System.out.println("title: " + this.title);
            System.out.println("content: " + this.content);
        } else {
            this.title = "New Chat";
            this.content = "";
        }
    }
}
