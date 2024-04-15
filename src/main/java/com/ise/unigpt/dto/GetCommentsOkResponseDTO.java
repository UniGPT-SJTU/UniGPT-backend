package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetCommentsOkResponseDTO {
    private Integer total;
    private List<CommentDTO> comments;

    public GetCommentsOkResponseDTO(List<CommentDTO> comments) {
        this.total = comments.size();
        this.comments = comments;
    }
}
