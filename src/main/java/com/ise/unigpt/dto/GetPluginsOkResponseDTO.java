package com.ise.unigpt.dto;

import java.util.List;

import lombok.Data;

@Data
public class GetPluginsOkResponseDTO {
    private Integer total;
    private List<PluginBriefInfoDTO> bots;

    /**
     * 
     * @param total 总数
     * @param bots 机器人列表
     */
    public GetPluginsOkResponseDTO(int total, List<PluginBriefInfoDTO> bots) {
        this.total = total;
        this.bots = bots;
    }
}
