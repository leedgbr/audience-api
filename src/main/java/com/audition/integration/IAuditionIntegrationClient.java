package com.audition.integration;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;

public interface IAuditionIntegrationClient {

    List<AuditionPost> getPosts();

    AuditionPost getPost(final String id);

    List<Comment> getComments(String postId);
}
