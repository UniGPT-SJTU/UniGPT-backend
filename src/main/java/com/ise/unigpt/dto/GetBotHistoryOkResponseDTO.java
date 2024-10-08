package com.ise.unigpt.dto;

import com.ise.unigpt.model.History;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class GetBotHistoryOkResponseDTO {
    private Integer total;
    private List<HistoryItemDTO> histories;

    public GetBotHistoryOkResponseDTO(Integer total, List<History> histories) {
        this.total = total;
        this.histories = histories.stream().map(HistoryItemDTO::new).collect(Collectors.toList());
    }
}
