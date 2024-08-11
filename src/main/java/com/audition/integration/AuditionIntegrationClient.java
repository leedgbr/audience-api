package com.audition.integration;

import com.audition.common.exception.BusinessException;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${audition-source-url}")
    private URL baseUrl;

    public List<AuditionPost> getPosts() {
        ResponseEntity<AuditionPost[]> response = handle2xx(
            restTemplate.getForEntity(String.format("%s/posts", baseUrl), AuditionPost[].class));
        AuditionPost[] body = response.getBody();
        if (body == null) {
            throw new SystemException("Missing list content when fetching posts", response.getStatusCode().value());
        }
        return Arrays.asList(body);
    }

    public AuditionPost getPost(final String id) {
        AuditionPost post = getPostOnly(id);
        List<Comment> comments = getComments(id);
        post.setComments(comments);
        return post;
    }

    public List<Comment> getComments(String postId) {
        try {
            ResponseEntity<Comment[]> response = handle2xx(
                restTemplate.getForEntity(String.format("%s/posts/%s/comments", baseUrl, postId), Comment[].class));
            return getCommentsBody(response, postId);
        } catch (final HttpClientErrorException e) {
            // Unfortunately, there is no error response returned when trying to fetch comments for a post id that does
            // not exist.  This is the case for both possible URLs for fetching comments.  Since we aren't able to
            // differentiate between a post not existing vs there being no comments for a post we will need to return an
            // empty comment list in both of those situations.
            throw new SystemException("Unexpected error fetching comments by id", e);
        }
    }

    private AuditionPost getPostOnly(final String id) {
        try {
            ResponseEntity<AuditionPost> response = handle2xx(
                restTemplate.getForEntity(String.format("%s/posts/%s", baseUrl, id), AuditionPost.class));
            return getPostBody(response, id);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw BusinessException.newResourceNotFound(String.format("Cannot find a Post with id '%s'", id));
            }
            throw new SystemException("Unexpected error fetching post by id", e);
        }
    }

    private AuditionPost getPostBody(ResponseEntity<AuditionPost> response, String id) {
        AuditionPost post = response.getBody();
        if (post == null) {
            throw new SystemException(String.format("Missing content for Post with id '%s'", id),
                response.getStatusCode().value());
        }
        return post;
    }

    private List<Comment> getCommentsBody(ResponseEntity<Comment[]> response, String postId) {
        Comment[] comments = response.getBody();
        if (comments == null) {
            throw new SystemException(String.format("Missing content for Comments for Post with id '%s'", postId),
                response.getStatusCode().value());
        }
        return Arrays.asList(comments);
    }

    private <T> ResponseEntity<T> handle2xx(ResponseEntity<T> response) {
        // we need to make sure we don't accept any 2xx responses other than 200 as we don't expect to receive those.
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new SystemException("Non http 200 success when fetching posts",
                response.getStatusCode().value());
        }
        return response;
    }
}
