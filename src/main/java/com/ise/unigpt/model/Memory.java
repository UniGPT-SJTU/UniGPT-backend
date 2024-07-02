package com.ise.unigpt.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "memory")
public class Memory {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    private History history;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "memory")
    private List<MemoryItem> memoryItems;

    public Memory() {
        // not used
    }

    public Memory(History history) {
        this.history = history;
        this.memoryItems = new ArrayList<>();
    }
}
