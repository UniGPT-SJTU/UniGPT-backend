package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bot")
public class Bot {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "name")
    String name;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "description")
    String description;

    @Column(name = "base_model_api")
    String baseModelAPI;

    @Column(name = "is_published")
    boolean isPublished;

    @Column(name = "detail")
    String detail;

//    @Column(name = "photos")
//    String[] photos;

    @Column(name = "is_prompted")
    boolean isPrompted;

    @Column(name = "prompt_content")
    String promptContent;

//    @Column(name = "prompt_list")
//    String[] promptList;
}
