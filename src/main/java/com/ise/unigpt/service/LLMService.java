package com.ise.unigpt.service;

import com.ise.unigpt.model.History;

import dev.langchain4j.service.TokenStream;
import lombok.Data;


/**
 * 通用的大模型服务接口，供WebSocket部分后端开发者调用，使用样例见controller/ChatController.java
 */
public interface LLMService {

    @Data
    public class GenerateResponseOptions {
        private Boolean cover;
        private Boolean isUserAsk;

        public static GenerateResponseOptionsBuilder builder() {
            return new GenerateResponseOptionsBuilder();
        }
    }

    public class GenerateResponseOptionsBuilder {
        private Boolean cover;
        private Boolean isUserAsk;

        public GenerateResponseOptionsBuilder cover(Boolean cover) {
            this.cover = cover;
            return this;
        }

        public GenerateResponseOptionsBuilder isUserAsk(Boolean isUserAsk) {
            this.isUserAsk = isUserAsk;
            return this;
        }

        public GenerateResponseOptions build() {
            GenerateResponseOptions options = new GenerateResponseOptions();
            options.cover = this.cover;
            options.isUserAsk = this.isUserAsk;
            return options;
        }
    }
    /**
     * @brief 生成LLM的回复
     * 
     * @param history     大模型对话的历史上下文（不包含userMessage）
     * @param userMessage 用户的最后一条消息
     * @param generateResponseOptions 生成回复的选项，包括isUserAsk, cover等boolean参数
     * @return LLM基于对话补全生成的回复内容
     * @throws Exception 抛出异常
     */
    public TokenStream generateResponse(History history, String userMessage, GenerateResponseOptions options) throws Exception;
}
