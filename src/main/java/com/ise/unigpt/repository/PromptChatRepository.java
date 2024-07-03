package com.ise.unigpt.repository;

import com.ise.unigpt.model.PromptChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptChatRepository extends JpaRepository<PromptChat, Integer> {
}
