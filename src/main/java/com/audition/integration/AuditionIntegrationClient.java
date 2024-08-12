package com.audition.integration;

import com.audition.common.exception.BusinessException;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for the details of communication with the Audition source system via http.  This is currently done via
 * the RestTemplate.  Has the responsibility of mapping the response(s) received via RestTemplate into the model used by
 * the application.
 */
@Component
public class AuditionIntegrationClient implements IAuditionIntegrationClient {

    private static final String POST_NOT_FOUND = "POST_NOT_FOUND";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * The base url of the Audition source system, to use for all requests.
     */
    @Value("${audition-source-url}")
    private URL baseUrl;


    /**
     * Retrieve all posts from the source system.  If provided, filter by the user id.  Comments for the AuditionPosts
     * are not fetched by this operation.
     *
     * @param userId The userId to filter on
     * @return The list of AuditionPosts.
     */
    @Override
    public List<AuditionPost> getPosts(final String userId) {
        final ResponseEntity<AuditionPost[]> response = handle2xx(
            restTemplate.getForEntity(getPostUrl(userId), AuditionPost[].class));
        final AuditionPost[] body = response.getBody();
        if (body == null) {
            throw new SystemException("Missing list content when fetching posts", response.getStatusCode().value());
        }
        return Arrays.asList(body);
    }

    /**
     * Retrieves a single AuditionPost from the source system by id.  The Comments of the AuditionPost are also fetched
     * by this operation.
     *
     * @param id The id of the AuditionPost
     * @return The AuditionPost.
     */
    @Override
    public AuditionPost getPost(final String id) {
        final AuditionPost post = getPostOnly(id);
        final List<Comment> comments = getComments(id);
        post.setComments(comments);
        return post;
    }

    /**
     * Retrieves only the Comments of the AuditionPost with the supplied id.
     *
     * @param postId The id of the AuditionPost to fetch comments for
     * @return The list of Comments.
     * @throws SystemException If
     */
    @Override
    public List<Comment> getComments(final String postId) {
        final ResponseEntity<Comment[]> response = handle2xx(
            restTemplate.getForEntity(String.format("%s/posts/%s/comments", baseUrl, postId), Comment[].class));
        // Unfortunately, there is no error response returned when trying to fetch comments for a post id that does
        // not exist.  This is the case for both possible URLs for fetching comments.  Since we aren't able to
        // differentiate between a post not existing vs there being no comments for a post we will need to return an
        // empty comment list in both of those situations.
        return getCommentsBody(response, postId);
    }

    /**
     * Retrieves the AuditionPost with the supplied id from the source system, with no Comments.
     *
     * @param id The id of the AuditionPost
     * @return The AuditionPost.
     * @throws SystemException   if there was a http 4xx error returned from the source system, other than a 404.
     * @throws BusinessException if there was a http 404 error returned from the source system, which indicates the
     *                           client requested a post that does not exist.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private AuditionPost getPostOnly(final String id) {
        try {
            final ResponseEntity<AuditionPost> response = handle2xx(
                restTemplate.getForEntity(String.format("%s/posts/%s", baseUrl, id), AuditionPost.class));
            return getPostBody(response, id);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw BusinessException.newResourceNotFound(POST_NOT_FOUND);
            }
            throw new SystemException("Unexpected error fetching post by id", e);
        }
    }

    /**
     * Retrieves the AuditionPost from the source system response.
     *
     * @param response The source system response from RestTemplate
     * @param id       The id of the AuditionPost being fetched
     * @return The AuditionPost
     * @throws SystemException If there is no content in the body of the response.
     */
    private AuditionPost getPostBody(final ResponseEntity<AuditionPost> response, final String id) {
        final AuditionPost post = response.getBody();
        if (post == null) {
            throw new SystemException(String.format("Missing content for Post with id '%s'", id),
                response.getStatusCode().value());
        }
        return post;
    }

    /**
     * Retrieves the List of Comments from the source system response.
     *
     * @param response The source system response from RestTemplate
     * @param postId   The id of the AuditionPost for which Comments are being fetched
     * @return The List of Comments
     * @throws SystemException If there is no content in the body of the response.
     */
    private List<Comment> getCommentsBody(final ResponseEntity<Comment[]> response, final String postId) {
        final Comment[] comments = response.getBody();
        if (comments == null) {
            throw new SystemException(String.format("Missing content for Comments for Post with id '%s'", postId),
                response.getStatusCode().value());
        }
        return Arrays.asList(comments);
    }

    /**
     * Ensures we don't try to handle a response from the source system when the http status code was a 2xx code other
     * than 200.
     *
     * @param response The response received from the source system.
     * @param <T>      The entity type in the response.
     * @return The response entity if no issues.
     * @throws SystemException If a 2xx http response code other than 200 was received.
     */
    private <T> ResponseEntity<T> handle2xx(final ResponseEntity<T> response) {
        // we need to make sure we don't accept any 2xx responses other than 200 as we don't expect to receive those.
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new SystemException("Non http 200 success when fetching posts",
                response.getStatusCode().value());
        }
        return response;
    }

    /**
     * Constructs the source system url for fetching all posts, including the user id query parameter if required.
     *
     * @param userId The user id to filter by, if applicable.
     * @return The source system url.
     */
    private String getPostUrl(final String userId) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s/posts", baseUrl));
        if (!StringUtils.isBlank(userId)) {
            builder.append(String.format("?userId=%s", userId));
        }
        return builder.toString();
    }

}
