package com.ise.unigpt.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tool")
public class Tool {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    // list of parameters
    @ElementCollection
    @Column(name = "parameters", length = 3000)
    private List<String> parameters;

    @Column(name = "fileBody", nullable = false, columnDefinition = "LONGTEXT")
    private String fileBody;
}
