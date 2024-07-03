package com.ise.unigpt.dto;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.History;
import lombok.Data;

import java.util.Date;

@Data
public class ChatDTO {
    private Integer id;
    private String content;
    private String avatar;
    private String name;
    private ChatType type;

    public ChatDTO(Chat chat) {
        this.id = chat.getId();
        this.content = chat.getContent();
        this.type = chat.getType();

        History history = chat.getHistory();
        switch (chat.getType()) {
            case USER:
                this.avatar = history.getUser().getAvatar();
                this.name = history.getUser().getName();
                break;
            case BOT:
                this.avatar = history.getBot().getAvatar();
                this.name = history.getBot().getName();
                break;

            case SYSTEM:
            default:
                this.avatar = "";
                this.name = "System";
                break;
        }
    }

    public ChatDTO(Integer id, String content, Date time, String avatar, String name, ChatType type) {
        this.id = id;
        this.content = content;
        this.avatar = avatar;
        this.name = name;
        this.type = type;
    }
}
