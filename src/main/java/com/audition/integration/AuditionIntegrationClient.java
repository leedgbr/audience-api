package com.audition.integration;

import com.audition.common.exception.BusinessException;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Qualifier("auditionRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        ResponseEntity<AuditionPost[]> response = handle2xx(restTemplate.getForEntity("/posts", AuditionPost[].class));
        AuditionPost[] body = response.getBody();
        if (body == null) {
            throw new SystemException("Missing list content when fetching posts", response.getStatusCode().value());
        }
        return Arrays.asList(body);
    }

    public AuditionPost getPostById(final String id) {
        try {
            ResponseEntity<AuditionPost> response = handle2xx(
                restTemplate.getForEntity(String.format("/posts/%s", id), AuditionPost.class));
            return getBody(response, id);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw BusinessException.newResourceNotFound(String.format("Cannot find a Post with id '%s'", id));
            }
            throw new SystemException("Unexpected error fetching post by id", e);
        }
    }

    private AuditionPost getBody(ResponseEntity<AuditionPost> response, String id) {
        AuditionPost post = response.getBody();
        if (post == null) {
            throw new SystemException(String.format("Missing content for Post with id '%s'", id),
                response.getStatusCode().value());
        }
        return post;
    }

    private <T> ResponseEntity<T> handle2xx(ResponseEntity<T> response) {
        // we need to make sure we don't accept any 2xx responses other than 200 as we don't expect to receive those.
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new SystemException("Non http 200 success when fetching posts",
                response.getStatusCode().value());
        }
        return response;
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
}
