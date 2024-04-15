package com.ise.unigpt.dto;

import com.ise.unigpt.model.Chat;
import lombok.Data;

import java.util.List;

@Data
public class GetBotHistoryOkResponseDTO {
    private Integer total;
    private List<Chat> chats;

    public GetBotHistoryOkResponseDTO(List<Chat> chats) {
        this.total = chats.size();
        this.chats = chats;
    }
}
