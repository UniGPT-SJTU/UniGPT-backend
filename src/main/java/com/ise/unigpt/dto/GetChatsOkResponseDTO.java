package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetChatsOkResponseDTO {
    private Integer total;
    private List<ChatDTO> chats;

    public GetChatsOkResponseDTO(Integer total, List<ChatDTO> chats) {
        this.total = total;
        this.chats = chats;
    }
}

