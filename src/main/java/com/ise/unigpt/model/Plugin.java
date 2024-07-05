package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "plugin")
public class Plugin {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    
}
