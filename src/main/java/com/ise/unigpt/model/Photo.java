package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

}
