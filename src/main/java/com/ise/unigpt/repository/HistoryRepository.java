package com.ise.unigpt.repository;

import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
}
