package com.ise.unigpt.dto;

import lombok.Data;

import com.ise.unigpt.model.Chat;

@Data
public class HistoryItemDTO {
    int id;
    String title;
    String content;

    public HistoryItemDTO(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public HistoryItemDTO(Chat chat) {
        this.id = chat.getId();
        this.title = chat.getType().toString(); // title需要由gpt总结，现在随便写一个
        this.content = chat.getContent();
    }
}
