package com.ise.unigpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat")
public class Chat {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JsonIncludeProperties({"id"})
    @JoinColumn(name = "history_id")
    @JsonIgnore
    private History history;

    @Column(name = "type")
    private ChatType type;

    @Column(name = "time")
    Date time;

    @Column(name = "content")
    private String content;
}
