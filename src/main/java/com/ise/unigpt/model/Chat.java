package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat")
public class Chat {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @ManyToOne
    @JsonIncludeProperties({"id"})
    @JoinColumn(name = "history_id")
    History history;

    @Column(name = "type")
    ChatType type;

    @Column(name = "content")
    String content;
}
