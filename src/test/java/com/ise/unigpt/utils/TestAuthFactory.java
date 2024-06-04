package com.ise.unigpt.utils;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;

public class TestAuthFactory {
    static public LoginRequestDTO createLoginRequestDTO() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("test");
        loginRequestDTO.setPassword("test");
        return loginRequestDTO;
    }

    static public Auth createAuth(User user) {
        Auth auth = new Auth();
        auth.setToken("test");
        auth.setUser(user);
        return auth;
    }
}
