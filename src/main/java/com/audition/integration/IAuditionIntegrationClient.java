package com.audition.integration;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;

public interface IAuditionIntegrationClient {

    List<AuditionPost> getPosts(String userID);

    AuditionPost getPost(String id);

    List<Comment> getComments(String postId);
}
