package com.ise.unigpt.repository;

import com.ise.unigpt.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends JpaRepository<Bot, Integer> {

}
