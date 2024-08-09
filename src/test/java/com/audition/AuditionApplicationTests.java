package com.audition;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            .andExpect(content().string(containsString("[]")));
    }

    @Test
    void shouldGetPostWithId() throws Exception {
        this.mockMvc.perform(get("/post/1234"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("")));
    }
}
