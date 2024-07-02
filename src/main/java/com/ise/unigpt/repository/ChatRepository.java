package com.ise.unigpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ise.unigpt.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    
}
