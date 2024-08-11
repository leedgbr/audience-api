package com.audition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class ActuatorEndpointTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFetchActuatorHealth() throws Exception {
        this.mockMvc
            .perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldFetchActuatorInfo() throws Exception {
        this.mockMvc
            .perform(get("/actuator/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.app.details").value("this is really useful detail"));
    }

    @Test
    void shouldNotFetchActuatorUnexposedEndpoint() throws Exception {
        this.mockMvc
            .perform(get("/actuator/beans"))
            .andExpect(status().isForbidden());
    }
}
