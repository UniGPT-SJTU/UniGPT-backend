package com.ise.unigpt.dto;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ise.unigpt.dto.HistoryItemDTO;

@Data
public class GetBotHistoryOkResponseDTO {
    private Integer total;
    private List<HistoryItemDTO> histories;

    public GetBotHistoryOkResponseDTO(List<History> histories) {
        this.total = histories.size();
        this.histories = histories.stream().map(HistoryItemDTO::new).collect(Collectors.toList());
    }
}
