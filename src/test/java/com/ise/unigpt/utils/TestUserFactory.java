package com.ise.unigpt.utils;

import com.ise.unigpt.model.User;

public class TestUserFactory {
    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("user1");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar1");
        user.setAsAdmin(false);
        return user;
    }    
}
