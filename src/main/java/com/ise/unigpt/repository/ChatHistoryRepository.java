package com.ise.unigpt.repository;

import com.ise.unigpt.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {
}
