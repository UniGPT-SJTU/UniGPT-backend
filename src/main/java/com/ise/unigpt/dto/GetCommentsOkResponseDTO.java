package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetCommentsOkResponseDTO {
    private Integer total;
    private List<CommentDTO> comments;

    public GetCommentsOkResponseDTO(Integer total, List<CommentDTO> comments) {
        this.total = total;
        this.comments = comments;
    }
}
