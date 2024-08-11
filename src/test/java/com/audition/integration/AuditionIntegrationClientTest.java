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
import com.audition.model.Comment;
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
        assertThat(PostFixture.getExpectedPosts(), contains(posts.toArray()));
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
    public void getPost() {
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
        AuditionPost post = client.getPost("123");
        mockServer.verify();
        assertEquals(PostFixture.getExpectedPostWithComments(), post);
    }

    @Test
    public void getPostNotFound() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
        BusinessException exception = assertThrows(
            BusinessException.class, () -> client.getPost("123")
        );
        mockServer.verify();
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals("Cannot find a Post with id '123'", exception.getDetail());
    }

    @Test
    public void getPostEmpty() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/post-empty.json"))
            );
        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost("123")
        );
        mockServer.verify();
        assertEquals("Missing content for Post with id '123'", exception.getMessage());
        assertEquals(200, exception.getStatusCode());
    }

    @Test
    public void getPost2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost("123")
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    public void getPostHttpClientErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.FORBIDDEN));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost("123")
        );
        assertEquals("Unexpected error fetching post by id", exception.getMessage());
        assertEquals("403 Forbidden: [no body]", exception.getCause().getMessage());
    }

    @Test
    public void getPostErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getPost("123")
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }

    @Test
    public void getComments() {
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/comments.json"))
            );
        List<Comment> comments = client.getComments("123");
        mockServer.verify();
        assertEquals(CommentFixture.getExpectedComments(), comments);
    }

    @Test
    public void getCommentsEmpty() {
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/comments-empty.json"))
            );
        SystemException exception = assertThrows(
            SystemException.class, () -> client.getComments("123")
        );
        mockServer.verify();
        assertEquals("Missing content for Comments for Post with id '123'", exception.getMessage());
        assertEquals(200, exception.getStatusCode());
    }

    @Test
    public void getComments2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getComments("123")
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    public void getCommentsHttpClientErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.FORBIDDEN));

        SystemException exception = assertThrows(
            SystemException.class, () -> client.getComments("123")
        );
        assertEquals("Unexpected error fetching comments by id", exception.getMessage());
        assertEquals("403 Forbidden: [no body]", exception.getCause().getMessage());
    }

    @Test
    public void getCommentsErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(commentsUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getComments("123")
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }
}

