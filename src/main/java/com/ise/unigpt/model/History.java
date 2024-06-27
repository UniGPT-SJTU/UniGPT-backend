package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    // TODO@creeper: 将Bot.promptChats和History.promptChats隔离开

    /**
     * @brief 对话历史的提示对话（已经嵌入了用户填写的表单）
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PromptChat> promptChats;

    /**
     * @brief 存储机器人的promptKey和用户填写的promptValue的映射关系
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "history_prompt_key_value_pairs",
            joinColumns = @JoinColumn(name = "history_id"))
    @MapKeyColumn(name = "prompt_key", columnDefinition = "VARCHAR(255)")
    @Column(name = "prompt_value", columnDefinition = "LONGTEXT")
    private Map<String, String> promptKeyValuePairs;

    /**
     * @brief 存储用户和机器人的对话
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Chat> chats;

    public History() {
        // not used
    }

    public History(User user, Bot bot, Map<String, String> promptKeyValuePairs, List<PromptChat> promptChats) {
        this.user = user;
        this.bot = bot;
        this.chats = new ArrayList<>();
        this.promptKeyValuePairs = promptKeyValuePairs;
        this.promptChats = promptChats;
    }

    public Date getLatestChatTime() {
        if (chats.isEmpty()) {
            return new Date(); // 或者你可以定义一个默认的时间
        } else {
            return chats.get(chats.size() - 1).getTime();
        }
    }

}
