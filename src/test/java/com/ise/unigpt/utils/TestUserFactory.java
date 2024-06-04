package com.ise.unigpt.utils;

import com.ise.unigpt.model.User;

import java.util.ArrayList;
import java.util.List;

public class TestUserFactory {
    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("user1");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar1");
        user.setAsAdmin(false);
        user.setDisabled(false);
        return user;
    }

    public static User createAdmin() {
        User user = new User();
        user.setId(1);
        user.setName("user1");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar1");
        user.setAsAdmin(true);
        return user;
    }

    public static User createUser2() {
        User user = new User();
        user.setId(2);
        user.setName("user2");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar2");
        user.setAsAdmin(false);
        return user;
    }

    public static User createUser3() {
        User user = new User();
        user.setId(3);
        user.setName("user3");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar3");
        user.setAsAdmin(false);
        return user;
    }

    public static User createAdminUser() {
        User user = new User();
        user.setId(4);
        user.setName("admin");
        user.setEmail("admin@hellomail");
        user.setAvatar("avatar4");
        user.setAsAdmin(true);
        return user;
    }
}
