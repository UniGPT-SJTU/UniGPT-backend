package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetBotsOkResponseDTO {
    private Integer total;
    private List<BotBriefInfoDTO> bots;

    public GetBotsOkResponseDTO(List<BotBriefInfoDTO> bots) {
        this.total = bots.size();
        this.bots = bots;
    }

    public GetBotsOkResponseDTO(int total, List<BotBriefInfoDTO> bots) {
        this.total = total;
        this.bots = bots;
    }
}
