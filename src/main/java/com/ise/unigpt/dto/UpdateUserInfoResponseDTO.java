package com.ise.unigpt.dto;

import lombok.Data;

@Data
public class UpdateUserInfoResponseDTO {
    private Boolean ok;
    public UpdateUserInfoResponseDTO(Boolean ok) {
        this.ok = ok;
    }
}
