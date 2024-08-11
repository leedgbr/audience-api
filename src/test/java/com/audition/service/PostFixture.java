package com.audition.service;

import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;

public class PostFixture {

    static List<AuditionPost> getExpectedPosts() {
        AuditionPost post1 = new AuditionPost();
        post1.setId(1);
        post1.setUserId(1);
        post1.setTitle("title1");
        post1.setBody("body1");

        AuditionPost post2 = new AuditionPost();
        post2.setId(2);
        post2.setUserId(1);
        post2.setTitle("title2");
        post2.setBody("body2");

        return Arrays.asList(post1, post2);
    }

    static List<AuditionPost> getExpectedPostsForUser() {
        AuditionPost post1 = new AuditionPost();
        post1.setId(7);
        post1.setUserId(5);
        post1.setTitle("title7");
        post1.setBody("body7");

        AuditionPost post2 = new AuditionPost();
        post2.setId(74);
        post2.setUserId(5);
        post2.setTitle("title74");
        post2.setBody("body74");

        return Arrays.asList(post1, post2);
    }

    static AuditionPost getExpectedPost() {
        AuditionPost post = new AuditionPost();
        post.setId(123);
        post.setUserId(1);
        post.setTitle("title123");
        post.setBody("body123");
        return post;
    }
}
