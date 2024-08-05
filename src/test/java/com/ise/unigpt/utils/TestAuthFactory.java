package com.ise.unigpt.utils;

import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;

public class TestAuthFactory {
    // static public LoginRequestDTO createLoginRequestDTO() throws Exception {
    //     LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
    //     loginRequestDTO.setUsername("test");
    //     loginRequestDTO.setPassword("test");

    //     ReflectionTestUtils.assertNoNullFields(loginRequestDTO);
    //     return loginRequestDTO;
    // }

    static public Auth createAuth(User user) throws Exception {
        Auth auth = new Auth();
        auth.setToken("test");
        auth.setUser(user);
        auth.setId(1);

        ReflectionTestUtils.assertNoNullFields(auth);
        return auth;
    }
}
