package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class CreateBotHistoryOkResponseDTO {
    private Boolean ok;
    private String message;
    private Integer historyid;

    public CreateBotHistoryOkResponseDTO(Boolean ok, String message, Integer historyid) {
        this.ok = ok;
        this.message = message;
        this.historyid = historyid;
    }
}
