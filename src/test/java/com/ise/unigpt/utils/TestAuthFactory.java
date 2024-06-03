package com.ise.unigpt.utils;

import com.ise.unigpt.dto.LoginRequestDTO;

public class TestAuthFactory {
    static public LoginRequestDTO createLoginRequestDTO() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("test");
        loginRequestDTO.setPassword("test");
        return loginRequestDTO;
    }
}
