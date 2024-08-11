package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.BusinessException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditionServiceTest {

    private AuditionService service;

    @BeforeEach
    void setUp() {
        service = new AuditionService(new StubIntegrationClient(), new Validator());
    }

    @Test
    void posts() {
        final String userId = null;
        final List<AuditionPost> post = service.getPosts(userId);
        assertEquals(PostFixture.getExpectedPosts(), post);
    }

    @Test
    void postsFilteredByUserId() {
        final String userId = "5";
        final List<AuditionPost> post = service.getPosts(userId);
        assertEquals(PostFixture.getExpectedPostsForUser(), post);
    }

    @Test
    void postById() {
        final AuditionPost post = service.getPostById("123");
        assertEquals(PostFixture.getExpectedPost(), post);
    }

    @Test
    void postByIdValidationShouldBeWiredUp() {
        final BusinessException exception = assertThrows(
            BusinessException.class, () -> service.getPostById("   ")
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_REQUIRED", exception.getDetail());
    }

    @Test
    void commentsByPostId() {
        final List<Comment> comments = service.getComments("123");
        assertEquals(CommentFixture.getExpectedComments(), comments);
    }

    @Test
    void commentsByPostIdValidationShouldBeWiredUp() {
        final BusinessException exception = assertThrows(
            BusinessException.class, () -> service.getComments("   ")
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_REQUIRED", exception.getDetail());
    }
}

