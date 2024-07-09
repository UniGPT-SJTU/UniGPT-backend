package com.ise.unigpt.model;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tool_exec_req_memory_item")
public class ToolExecutionRequestMemoryItem {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "arguments")
    private String arguments;

    @ManyToOne
    private MemoryItem memoryItem;

    public ToolExecutionRequestMemoryItem() {
        // not used
    }

    public ToolExecutionRequestMemoryItem(ToolExecutionRequest toolExecutionRequest, MemoryItem memoryItem) {
        this.id = toolExecutionRequest.id();
        this.name = toolExecutionRequest.name();
        this.arguments = toolExecutionRequest.arguments();
        this.memoryItem = memoryItem;
    }

    public ToolExecutionRequest toToolExecutionRequest() {
        return ToolExecutionRequest.builder()
                .id(id)
                .name(name)
                .arguments(arguments)
                .build();
    }
}
