package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ise.unigpt.parameters.LLMArgs.LLMArgs;

@Data
@Entity
@Table(name = "history")
public class History {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    /**
     * @brief 存储机器人的promptKey和用户填写的promptValue的映射关系
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "history_prompt_key_value_pairs",
            joinColumns = @JoinColumn(name = "history_id"))
    @MapKeyColumn(name = "prompt_key", columnDefinition = "VARCHAR(255)")
    @Column(name = "prompt_value", columnDefinition = "LONGTEXT")
    private Map<String, String> promptKeyValuePairs;

    @Column(name = "last_active_time")
    private Date lastActiveTime;

    @Embedded
    private LLMArgs llmArgs;

    /**
     * @brief 存储用户和机器人的对话
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "history")
    private List<Chat> chats;


    public History() {
        // not used
    }

    public History(User user, Bot bot, Map<String, String> promptKeyValuePairs, LLMArgs llmArgs) {
        this.user = user;
        this.bot = bot;
        this.chats = new ArrayList<>();
        this.promptKeyValuePairs = promptKeyValuePairs;
        this.lastActiveTime = new Date();
        this.llmArgs = llmArgs;
    }

    public Date getLatestChatTime() {
        return lastActiveTime;
    }

}
