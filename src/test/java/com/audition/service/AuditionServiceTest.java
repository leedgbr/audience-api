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
    public void setUp() {
        service = new AuditionService(new StubIntegrationClient(), new Validator());
    }

    @Test
    public void posts() {
        String userId = null;
        List<AuditionPost> post = service.getPosts(userId);
        assertEquals(PostFixture.getExpectedPosts(), post);
    }

    @Test
    public void postsFilteredByUserId() {
        String userId = "5";
        List<AuditionPost> post = service.getPosts(userId);
        assertEquals(PostFixture.getExpectedPostsForUser(), post);
    }

    @Test
    public void postById() {
        AuditionPost post = service.getPostById("123");
        assertEquals(PostFixture.getExpectedPost(), post);
    }

    @Test
    public void postByIdValidationShouldBeWiredUp() {
        BusinessException exception = assertThrows(
            BusinessException.class, () -> service.getPostById("   ")
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_REQUIRED", exception.getDetail());
    }

    @Test
    public void commentsByPostId() {
        List<Comment> comments = service.getComments("123");
        assertEquals(CommentFixture.getExpectedComments(), comments);
    }

    @Test
    public void commentsByPostIdValidationShouldBeWiredUp() {
        BusinessException exception = assertThrows(
            BusinessException.class, () -> service.getComments("   ")
        );
        assertEquals("Validation", exception.getTitle());
        assertEquals("id", exception.getField());
        assertEquals("POST_ID_REQUIRED", exception.getDetail());
    }
}

