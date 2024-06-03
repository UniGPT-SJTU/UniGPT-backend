package com.ise.unigpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ise.unigpt.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

}
