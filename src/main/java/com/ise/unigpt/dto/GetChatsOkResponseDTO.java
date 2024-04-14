package com.ise.unigpt.dto;

import com.ise.unigpt.model.Chat;
import lombok.Data;

import java.util.List;

@Data
public class GetChatsOkResponseDTO {
    private Integer total;
    private List<ChatDTO> chats;

    public GetChatsOkResponseDTO(List<ChatDTO> chats) {
        this.total = chats.size();
        this.chats = chats;
    }
}

