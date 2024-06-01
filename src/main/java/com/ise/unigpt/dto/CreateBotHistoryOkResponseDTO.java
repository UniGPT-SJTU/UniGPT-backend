package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class CreateBotHistoryOkResponseDTO {
    private Boolean ok;
    private String message;
    private Integer historyid;
    private String userAsk;

    public CreateBotHistoryOkResponseDTO(Boolean ok, String message, Integer historyid, String userAsk) {
        this.ok = ok;
        this.message = message;
        this.historyid = historyid;
        this.userAsk = userAsk;
    }
}
