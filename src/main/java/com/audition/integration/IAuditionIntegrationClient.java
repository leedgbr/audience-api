package com.audition.integration;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;

/**
 * Defines the operations on an Audition store.
 */
public interface IAuditionIntegrationClient {

    /**
     * Retrieve all posts.  If provided, filter by the user id.
     *
     * @param userId The userId to filter on
     * @return The list of AuditionPosts.
     */
    List<AuditionPost> getPosts(String userId);

    /**
     * Retrieve single post.
     *
     * @param id The id of the post to retrieve.
     * @return The AuditionPost.
     */
    AuditionPost getPost(String id);

    /**
     * Retrieve all comments for a post.
     *
     * @param postId The id of the post to retrieve comments for.
     * @return The list of Comments.
     */
    List<Comment> getComments(String postId);
}
