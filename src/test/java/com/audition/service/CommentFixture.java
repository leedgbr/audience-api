package com.audition.service;

import com.audition.model.Comment;
import java.util.Arrays;
import java.util.List;

public class CommentFixture {

    public static List<Comment> getExpectedComments() {
        final Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setName("name1");
        comment1.setEmail("email1");
        comment1.setBody("body1");

        final Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setName("name2");
        comment2.setEmail("email2");
        comment2.setBody("body2");

        return Arrays.asList(comment1, comment2);
    }

}
