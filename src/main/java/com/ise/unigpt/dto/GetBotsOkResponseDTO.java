package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetBotsOkResponseDTO {
    private Integer total;
    private List<BotBriefInfoDTO> bots;

    /**
     * 
     * @param total 总数
     * @param bots 机器人列表
     */
    public GetBotsOkResponseDTO(int total, List<BotBriefInfoDTO> bots) {
        this.total = total;
        this.bots = bots;
    }
}
