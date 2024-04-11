package com.ise.unigpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateBotRequestDTO {

    private String name;
    private String avatar;
    private String description;
    private String baseModelAPI;
    private boolean isPublished;
    private String detail;
    private List<String> photos;
    private boolean isPrompted;
    private String promptContent;
    private List<String> promptList;

}
