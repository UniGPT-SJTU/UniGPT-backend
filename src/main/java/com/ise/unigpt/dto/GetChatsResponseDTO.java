package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetChatsResponseDTO {
    private Integer total;
    private List<ChatDTO> chats;

    public GetChatsResponseDTO(List<ChatDTO> chats) {
        this.total = chats.size();
        this.chats = chats;
    }
}

