package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bot")
public class Bot {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "description")
    private String description;

    @Column(name = "base_model_api")
    private String baseModelAPI;

    @Column(name = "is_published")
    private boolean isPublished;

    @Column(name = "detail")
    private String detail;

//    @Column(name = "photos")
//    String[] photos;

    @Column(name = "is_prompted")
    private boolean isPrompted;

    @Column(name = "prompt_content")
    private String promptContent;

//    @Column(name = "prompt_list")
//    String[] promptList;
}
