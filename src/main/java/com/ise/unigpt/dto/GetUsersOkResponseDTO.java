package com.ise.unigpt.dto;

import java.util.List;

import lombok.Data;

@Data
public class GetUsersOkResponseDTO {

    private List<UserBriefInfoDTO> users;
    private Integer total;

    public GetUsersOkResponseDTO(List<UserBriefInfoDTO> users, Integer total) {
        this.users = users;
        this.total = total;
    }

    public GetUsersOkResponseDTO() {
    }

    public GetUsersOkResponseDTO(Integer total, List<UserBriefInfoDTO> users) {
        this.users = users;
        this.total = total;
    }

}
