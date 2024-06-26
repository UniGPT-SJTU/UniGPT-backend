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
    private Date time;
    private String avatar;
    private String name;
    private ChatType type;

    public ChatDTO(Chat chat) {
        this.id = chat.getId();
        this.content = chat.getContent();
        this.time = chat.getTime();
        this.type = chat.getType();

        History history = chat.getHistory();
        this.avatar = chat.getType() == ChatType.BOT ?
                history.getBot().getAvatar() :
                history.getUser().getAvatar();
        this.name = chat.getType() == ChatType.BOT ?
                history.getBot().getName() :
                history.getUser().getName();
    }

    public ChatDTO(Integer id, String content, Date time, String avatar, String name, ChatType type) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.avatar = avatar;
        this.name = name;
        this.type = type;
    }
}
