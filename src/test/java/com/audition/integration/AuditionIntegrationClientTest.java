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

import com.audition.common.exception.BusinessException;
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
class AuditionIntegrationClientTest {

    @Autowired
    private AuditionIntegrationClient client;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private URI postsUri;
    private URI postByIdUri;
    private URI commentsUri;

    @BeforeEach
    public void init() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        postsUri = new URI("http://audience-post-source-system/posts");
        postByIdUri = new URI("http://audience-post-source-system/posts/123");
        commentsUri = new URI("http://audience-post-source-system/posts/123/comments");
    }

    @Test
    public void getAllPosts() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/posts.json"))
            );
        List<AuditionPost> posts = client.getPosts();
        mockServer.verify();
        assertThat(Fixture.getExpectedPosts(), contains(posts.toArray()));
    }

    @Test
    public void getAllPostsEmptyList() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/posts-empty-list.json"))
            );
        List<AuditionPost> posts = client.getPosts();
        mockServer.verify();
        assertThat(posts, is(empty()));
    }

    @Test
    public void getAllPosts2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPosts()
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    public void getAllPostsErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getPosts()
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }

    @Test
    public void getPostById() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/post.json"))
            );
        AuditionPost post = client.getPostById("123");
        mockServer.verify();
        assertEquals(Fixture.getExpectedPost(), post);
    }

    @Test
    public void getPostByIdNotFound() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
        BusinessException exception = assertThrows(
            BusinessException.class, () -> client.getPostById("123")
        );
        mockServer.verify();
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals("Cannot find a Post with id '123'", exception.getDetail());
    }

    @Test
    public void getPostByIdEmpty() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/post-empty.json"))
            );
        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPostById("123")
        );
        mockServer.verify();
        assertEquals("Missing content for Post with id '123'", exception.getMessage());
        assertEquals(200, exception.getStatusCode());
    }

    @Test
    public void getPostById2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPostById("123")
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    public void getPostByIdHttpClientErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.FORBIDDEN));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPostById("123")
        );
        assertEquals("Unexpected error fetching post by id", exception.getMessage());
        assertEquals("403 Forbidden: [no body]", exception.getCause().getMessage());
    }

    @Test
    public void getPostByIdErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getPostById("123")
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }

    @Test
    public void getPostByIdWithComments() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/post.json"))
            );
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/comments.json"))
            );
        AuditionPost post = client.getPostByIdWithComments("123");
        mockServer.verify();
        assertEquals(Fixture.getExpectedPostWithComments(), post);
    }

}

