package com.audition;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-local.yml")
class AuditionApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGetAllPosts() throws Exception {
        this.mockMvc.perform(get("/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(greaterThan(50))))
            .andExpect(jsonPath("$", hasItem(Map.of(
                "userId", 1,
                "id", 2,
                "title", "qui est esse",
                "body",
                "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"))))
            .andExpect(jsonPath("$", hasItem(Map.of(
                "userId", 8,
                "id", 71,
                "title", "et iusto veniam et illum aut fuga",
                "body",
                "occaecati a doloribus\niste saepe consectetur placeat eum voluptate dolorem et\nqui quo quia voluptas\nrerum ut id enim velit est perferendis"))));
    }

    @Test
    void shouldGetPostWithId() throws Exception {
        this.mockMvc.perform(get("/posts/8"))
            .andExpect(status().isOk())
            .andExpect(content().json(Fixture.readFile("com/audition/post.json")));
    }

    @Test
    void shouldReturnBusinessErrorWhenPostWithIdDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/posts/9999999999"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(Fixture.readFile("com/audition/post-business-error.json")));
    }

    @Test
    void shouldReturnValidationErrorWhenGetPostInputInvalid() throws Exception {
        this.mockMvc.perform(get("/posts/     "))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(Fixture.readFile("com/audition/post-business-error-validation.json")));
    }

    @Test
    void shouldGetComments() throws Exception {
        this.mockMvc.perform(get("/posts/8/comments"))
            .andExpect(status().isOk())
            .andExpect(content().json(Fixture.readFile("com/audition/comments.json")));
    }

    @Test
    void shouldReturnEmptyCommentsListWhenCommentsForPostWithIdDoNotExist() throws Exception {
        // The downstream system does not provide a way for us to distinguish between a post having no comments vs the
        // post not existing.  So we need to return an empty list.
        this.mockMvc.perform(get("/posts/9999999999/comments"))
            .andExpect(status().isOk())
            .andExpect(content().json(Fixture.readFile("com/audition/comments-for-non-existent-post.json")));
    }

    @Test
    void shouldReturnValidationErrorWhenGetCommentsInputInvalid() throws Exception {
        this.mockMvc.perform(get("/posts/ABCD/comments"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(Fixture.readFile("com/audition/comments-business-error-validation.json")));
    }
}