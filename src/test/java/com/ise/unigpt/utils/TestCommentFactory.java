package com.ise.unigpt.utils;

import com.ise.unigpt.model.*;

import java.util.Date;

public class TestCommentFactory {

    public static Comment createComment(User user, Bot bot) throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("comment1");
        comment.setUser(user);
        comment.setBot(bot);
        comment.setTime(new Date());

        ReflectionTestUtils.assertNoNullFields(comment);
        return comment;
    }
}