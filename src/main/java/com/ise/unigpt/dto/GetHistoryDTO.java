package com.ise.unigpt.dto;

import lombok.Data;
import com.ise.unigpt.model.History;

@Data
public class GetHistoryDTO {
    Integer historyId;
    Integer userId;
    Integer botId;

    public GetHistoryDTO(Integer historyId, Integer userId, Integer botId) {
        this.historyId = historyId;
        this.userId = userId;
        this.botId = botId;
    }

    public GetHistoryDTO(History history) {
        this.historyId = history.getId();
        this.userId = history.getUser().getId();
        this.botId = history.getBot().getId();
    }

}
