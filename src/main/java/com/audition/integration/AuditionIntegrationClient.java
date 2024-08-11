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

    public AuditionPost getPostById(final String id) {
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

    public AuditionPost getPostByIdWithComments(final String id) {
        AuditionPost post = getPostById(id);
        List<Comment> comments = getPostComments(id);
        post.setComments(comments);
        return post;
    }

    private List<Comment> getPostComments(String id) {
        try {
            ResponseEntity<Comment[]> response = handle2xx(
                restTemplate.getForEntity(String.format("%s/posts/%s/comments", baseUrl, id), Comment[].class));
            return getCommentsBody(response, id);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw BusinessException.newResourceNotFound(String.format("Cannot find a Post with id '%s'", id));
            }
            throw new SystemException("Unexpected error fetching post by id", e);
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.


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
