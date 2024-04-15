package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class CreateCommentRequestDTO {

    private String content;
    private Integer botId;
    private Integer userId;

    public CreateCommentRequestDTO(String content, Integer botId, Integer userId) {
        this.content = content;
        this.botId = botId;
        this.userId = userId;
    }

}
