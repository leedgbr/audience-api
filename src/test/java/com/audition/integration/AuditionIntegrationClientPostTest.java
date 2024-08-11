package com.audition.integration;

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
class AuditionIntegrationClientPostTest {

    public static final String POST_ID = "123";

    @Autowired
    private AuditionIntegrationClient client;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private URI postByIdUri;
    private URI commentsUri;

    @BeforeEach
    void init() throws URISyntaxException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        postByIdUri = new URI("http://audience-post-source-system/posts/123");
        commentsUri = new URI("http://audience-post-source-system/posts/123/comments");
    }

    @Test
    void shouldGetPost() {
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
        final AuditionPost post = client.getPost(POST_ID);
        mockServer.verify();
        assertEquals(PostFixture.getExpectedPostWithComments(), post);
    }

    @Test
    void shouldGetPostNotFound() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
        final BusinessException exception = assertThrows(
            BusinessException.class, () -> client.getPost(POST_ID)
        );
        mockServer.verify();
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals("Cannot find a Post with id '123'", exception.getDetail());
    }

    @Test
    void shouldGetPostEmpty() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ClassPathResource("com/audition/integration/post-empty.json"))
            );
        final SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost(POST_ID)
        );
        mockServer.verify();
        assertEquals("Missing content for Post with id '123'", exception.getMessage());
        assertEquals(200, exception.getStatusCode());
    }

    @Test
    void shouldGetPost2xxResponseOtherThan200() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        final SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost(POST_ID)
        );
        assertEquals("Non http 200 success when fetching posts", exception.getMessage());
        assertEquals(202, exception.getStatusCode());
    }

    @Test
    void shouldGetPostHttpClientErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.FORBIDDEN));

        final SystemException exception = assertThrows(
            SystemException.class, () -> client.getPost(POST_ID)
        );
        assertEquals("Unexpected error fetching post by id", exception.getMessage());
        assertEquals("403 Forbidden: [no body]", exception.getCause().getMessage());
    }

    @Test
    void shouldGetPostErrorResponse() {
        mockServer.expect(ExpectedCount.once(), requestTo(postByIdUri))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final RuntimeException exception = assertThrows(
            RuntimeException.class, () -> client.getPost(POST_ID)
        );
        assertEquals("500 Internal Server Error: [no body]", exception.getMessage());
    }
}

