package com.ise.unigpt.model;

import java.util.List;
import java.util.stream.Collectors;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "memory_item")
public class MemoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private ChatMessageType type;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "tool_exec_result_id")
    private String toolExecResultId;

    @Column(name = "tool_exec_result_tool_name")
    private String toolExecResultToolName;

    @OneToMany(mappedBy = "memoryItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ToolExecutionRequestMemoryItem> toolExecutionRequestMemoryItems;

    @ManyToOne
    @JoinColumn(name = "memory_id")
    private Memory memory;

    public MemoryItem() {
        // not used
    }

    public MemoryItem(Chat chat, Memory memory) {
        // 在创建历史时被调用
        switch (chat.getType()) {
            case USER:
                type = ChatMessageType.USER;
                break;
            case BOT:
                type = ChatMessageType.AI;
                break;
            case SYSTEM:
            default:
                type = ChatMessageType.SYSTEM;
                break;
        }
        this.content = chat.getContent();
        this.memory = memory;
    }

    public MemoryItem(ChatMessage chatMessage, Memory memory) {
        // 在PersistentChatMemoryStore中被调用
        // ChatMessage -> MemoryItem
        this.type = chatMessage.type();
        switch (chatMessage.type()) {
            case USER:
                this.content = ((UserMessage) chatMessage).singleText();
                break;

            case AI:
                AiMessage aiMessage = (AiMessage) chatMessage;
                if (aiMessage.text() != null) {
                    this.content = aiMessage.text();
                } else if(aiMessage.toolExecutionRequests() != null && !aiMessage.toolExecutionRequests().isEmpty()) {
                    this.toolExecutionRequestMemoryItems = aiMessage.toolExecutionRequests()
                            .stream()
                            .map(request -> new ToolExecutionRequestMemoryItem(request, this))
                            .collect(Collectors.toList());
                } else {
                    System.out.println("MemoryItem constructor cornercase");
                    assert(false);
                }
                break;

            case SYSTEM:
                this.content = ((SystemMessage) chatMessage).text();
                break;

            case TOOL_EXECUTION_RESULT:
            default:
                this.toolExecResultId = ((ToolExecutionResultMessage) chatMessage).id();
                this.toolExecResultToolName = ((ToolExecutionResultMessage) chatMessage).toolName();
                this.content = ((ToolExecutionResultMessage) chatMessage).text();
                break;
        }
        this.memory = memory;
    }

    public ChatMessage toChatMessage() {
        // MemoryItem -> ChatMessage
        switch (type) {
            case USER:
                return new UserMessage(content);

            case AI:
                if (toolExecutionRequestMemoryItems != null && !toolExecutionRequestMemoryItems.isEmpty()) {
                    System.out.println("building Ai message in MemoryItem::toChatMessage");
                    List<ToolExecutionRequest> requests = toolExecutionRequestMemoryItems
                            .stream()
                            .map(ToolExecutionRequestMemoryItem::toToolExecutionRequest)
                            .collect(Collectors.toList());
                    assert(requests != null);
                    return new AiMessage(requests);
                } else if(content != null) {
                    return new AiMessage(content);
                } else {
                    System.out.println("MemoryItem::toChatMessage: corner case");
                    assert(false);
                    return null;
                }
            case SYSTEM:
                return new SystemMessage(content);

            case TOOL_EXECUTION_RESULT:
            default:
                return new ToolExecutionResultMessage(toolExecResultId, toolExecResultToolName, content);
        }
    }
}
