package com.ise.unigpt.utils;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.model.Auth;

public class TestAuthFactory {
    static public LoginRequestDTO createLoginRequestDTO() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("test");
        loginRequestDTO.setPassword("test");
        return loginRequestDTO;
    }

    static public Auth createAuth() {
        Auth auth = new Auth();
        auth.setToken("test");
        return auth;
    }
}
