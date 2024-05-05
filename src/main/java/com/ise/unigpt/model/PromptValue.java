package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prompt_value")
public class PromptValue {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Column(name = "content")
    private String content;

    public PromptValue() {
        // not used
    }
    public PromptValue(History history, String content) {
        this.history = history;
        this.content = content;
    }
}
