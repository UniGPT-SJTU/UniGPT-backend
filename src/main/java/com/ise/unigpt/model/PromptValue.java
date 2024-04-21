package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "promt_value")
public class PromptValue {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Column(name = "content")
    private String content;
}
