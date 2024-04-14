package com.ise.unigpt.dto;

import com.ise.unigpt.model.Photo;
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
    private List<Photo> photos;
    private boolean isPrompted;
    private String promptContent;
    private List<String> promptList;

}