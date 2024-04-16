package com.ise.unigpt.repository;

import com.ise.unigpt.model.PromptChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptChatRepository extends JpaRepository<PromptChat, Integer> {
}
