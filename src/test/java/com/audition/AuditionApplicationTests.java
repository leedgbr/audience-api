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
            .andExpect(content().json(
                """
                    {
                      userId: 1,
                      id: 8,
                      title: "dolorem dolore est ipsam",
                      body: "dignissimos aperiam dolorem qui eum
                    facilis quibusdam animi sint suscipit qui sint possimus cum
                    quaerat magni maiores excepturi
                    ipsam ut commodi dolor voluptatum modi aut vitae"
                    }"""));
    }

    @Test
    void shouldReturnBusinessErrorWhenPostWithIdDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/posts/9999999999"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.detail").value("Cannot find a Post with id '9999999999'"))
            .andExpect(jsonPath("$.instance").value("/posts/9999999999"));
    }
}