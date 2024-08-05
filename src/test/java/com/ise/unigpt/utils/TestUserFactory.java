package com.ise.unigpt.utils;

import com.ise.unigpt.model.User;

public class TestUserFactory {
    public static User createUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("user1");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar1");
        user.setAsAdmin(false);
        user.setDisabled(false);
        // user.setPassword("password");
        user.setAccount("account");
        user.setCanvasUrl("canvasUrl");
        user.setDescription("description");

        ReflectionTestUtils.assertNoNullFields(user);
        return user;
    }

    public static User createAdmin() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("user1");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar1");
        user.setAsAdmin(true);
        // user.setPassword("password");
        user.setAccount("account");
        user.setCanvasUrl("canvasUrl");
        user.setDescription("description");

        ReflectionTestUtils.assertNoNullFields(user);
        return user;
    }

    public static User createUser2() throws Exception {
        User user = new User();
        user.setId(2);
        user.setName("user2");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar2");
        user.setAsAdmin(false);
        // user.setPassword("password");
        user.setAccount("account");
        user.setCanvasUrl("canvasUrl");
        user.setDescription("description");

        ReflectionTestUtils.assertNoNullFields(user);
        return user;
    }

    public static User createUser3() throws Exception {
        User user = new User();
        user.setId(3);
        user.setName("user3");
        user.setEmail("creeper@hellomail");
        user.setAvatar("avatar3");
        user.setAsAdmin(false);
        // user.setPassword("password");
        user.setAccount("account");
        user.setCanvasUrl("canvasUrl");
        user.setDescription("description");

        ReflectionTestUtils.assertNoNullFields(user);
        return user;
    }

    public static User createAdminUser() throws Exception {
        User user = new User();
        user.setId(4);
        user.setName("admin");
        user.setEmail("admin@hellomail");
        user.setAvatar("avatar4");
        user.setAsAdmin(true);

        ReflectionTestUtils.assertNoNullFields(user);
        return user;
    }
}
