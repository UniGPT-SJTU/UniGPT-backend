package com.ise.unigpt.dto;

import com.ise.unigpt.model.Chat;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.ise.unigpt.dto.HistoryItemDTO;

@Data
public class GetBotHistoryOkResponseDTO {
    private Integer total;
    private List<HistoryItemDTO> histories;

    public GetBotHistoryOkResponseDTO(List<Chat> chats) {
        this.histories = new ArrayList<>();
        this.total = chats.size();
        for (Chat chat : chats) {
            this.histories.add(new HistoryItemDTO(chat));
        }
    }
}