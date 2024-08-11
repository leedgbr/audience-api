package com.audition.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-local-stubbed.yml")
class AuditionIntegrationClientPostAllTest {

    @Autowired
    private AuditionIntegrationClient client;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private URI postsUri;
    private URI postsFilteredByUserUri;

    @BeforeEach
    void init() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        postsUri = new URI("http://audience-post-source-system/posts");
        postsFilteredByUserUri = new URI("http://audience-post-source-system/posts?userId=8");
    }

    @Test
    void shouldGetAllPosts() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/posts.json"))
            );
        final String userId = null;
        final List<AuditionPost> posts = client.getPosts(userId);
        mockServer.verify();
        assertThat(PostFixture.getExpectedPosts(), contains(posts.toArray()));
    }

    @Test
    void shouldGetAllPostsForUser() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsFilteredByUserUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/posts-for-user.json"))
            );
        final String userId = "8";
        final List<AuditionPost> posts = client.getPosts(userId);
        mockServer.verify();
        assertThat(PostFixture.getExpectedPostsForUser(), contains(posts.toArray()));
    }

    @Test
    void shouldGetAllPostsEmptyList() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/posts-empty-list.json"))
            );
        final String userId = null;
        final List<AuditionPost> posts = client.getPosts(userId);
        mockServer.verify();
        assertThat(posts, is(empty()));
    }

    @Test
    void shouldGetAllPosts2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));
        final String userId = null;
        final SystemException exception = assertThrows(
            SystemException.class, () -> client.getPosts(userId)
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    void shouldGetAllPostsErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final String userId = null;
        final RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getPosts(userId)
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }

}

