package com.ise.unigpt.repository;

import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Integer> {
    public Optional<Auth> findByToken(String token);
    public Optional<Auth> findByUser(User user);
}
