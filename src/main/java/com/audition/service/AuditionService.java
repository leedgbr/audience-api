package com.audition.service;

import com.audition.integration.IAuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Responsible for orchestrating audition related operations on a source system.
 */
@Service
public class AuditionService {

    private final IAuditionIntegrationClient client;
    private final Validator validator;

    /**
     * Creates a new AuditionService with the supplied source system client and validation.
     *
     * @param client    The source system client
     * @param validator The validator for verifying business rules
     */
    @Autowired
    public AuditionService(final IAuditionIntegrationClient client, final Validator validator) {
        this.client = client;
        this.validator = validator;
    }

    /**
     * Retrieves all posts from the source system client, filtered by the provided user id.
     *
     * @param userId The user to filter on
     * @return The list of AuditionPosts.
     */
    public List<AuditionPost> getPosts(final String userId) {
        return client.getPosts(userId);
    }

    /**
     * Retrieves a single post from the source system client. Validates to ensure the input parameters meet expected
     * constraints.
     *
     * @param postId The is of the post to retrieve
     * @return The AuditionPost.
     */
    public AuditionPost getPostById(final String postId) {
        validator.validate(postId);
        return client.getPost(postId);
    }

    /**
     * Retrieves all comments for a post from the source system client.  Validates to ensure the input parameters meet
     * expected constraints.
     *
     * @param postId The id of the post to retrieve comments for.
     * @return The list of Comments.
     */
    public List<Comment> getComments(final String postId) {
        validator.validate(postId);
        return client.getComments(postId);
    }
}


