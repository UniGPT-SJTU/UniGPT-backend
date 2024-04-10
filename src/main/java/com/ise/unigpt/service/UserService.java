package com.ise.unigpt.service;

import com.ise.unigpt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }



}
