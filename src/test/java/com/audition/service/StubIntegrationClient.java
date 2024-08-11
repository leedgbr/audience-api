package com.audition.service;

import com.audition.integration.IAuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.Arrays;
import java.util.List;

public class StubIntegrationClient implements IAuditionIntegrationClient {

    @Override
    public List<AuditionPost> getPosts(String userId) {
        if ("5".equals(userId)) {
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

    @Override
    public AuditionPost getPost(String id) {
        if ("123".equals(id)) {
            AuditionPost post = new AuditionPost();
            post.setId(123);
            post.setUserId(1);
            post.setTitle("title123");
            post.setBody("body123");
            return post;
        }
        throw new RuntimeException("problem");
    }

    @Override
    public List<Comment> getComments(String postId) {
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setName("name1");
        comment1.setEmail("email1");
        comment1.setBody("body1");

        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setName("name2");
        comment2.setEmail("email2");
        comment2.setBody("body2");

        return Arrays.asList(comment1, comment2);
    }
}
