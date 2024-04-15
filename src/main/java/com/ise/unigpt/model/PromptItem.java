package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PromptItem")
public class PromptItem {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JsonIncludeProperties({"id"})
    @JoinColumn(name = "history_id")
    private History history;

    @Column(name = "content")
    private String content;
}
