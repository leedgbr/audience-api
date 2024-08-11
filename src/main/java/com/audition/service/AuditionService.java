package com.audition.service;

import com.audition.integration.IAuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private final IAuditionIntegrationClient client;
    private final Validator validator;

    @Autowired
    public AuditionService(final IAuditionIntegrationClient client, final Validator validator) {
        this.client = client;
        this.validator = validator;
    }

    public List<AuditionPost> getPosts(final String userId) {
        return client.getPosts(userId);
    }

    public AuditionPost getPostById(final String postId) {
        validator.validate(postId);
        return client.getPost(postId);
    }

    public List<Comment> getComments(final String postId) {
        validator.validate(postId);
        return client.getComments(postId);
    }
}


