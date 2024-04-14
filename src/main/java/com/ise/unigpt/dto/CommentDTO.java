package com.ise.unigpt.dto;

import lombok.Data;
import com.ise.unigpt.model.Comment;

@Data
public class CommentDTO {
    
    private Integer id;
    private String content;
    private String time;
    private String avatar;
    private String name;
    private Integer botId;
    private Integer userId;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.time = comment.getTime();
        this.avatar = comment.getUser().getAvatar();
        this.name = comment.getUser().getName();
        this.botId = comment.getBot().getId();
        this.userId = comment.getUser().getId();
    }

    public CommentDTO(Integer id, String content, String time, String avatar, String name, Integer botId, Integer userId) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.avatar = avatar;
        this.name = name;
        this.botId = botId;
        this.userId = userId;
    }
}
