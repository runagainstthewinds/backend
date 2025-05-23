package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setupEnv() {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    @BeforeEach
    public void setup() throws Exception {
        // Register a test user before each test
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("jwtemail");
        user.setPassword("testpass");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoToken_whenAccessPublicEndpoint_thenOk() throws Exception {
        // Use POST /auth/login to login and return token with 200 OK
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new User() {{
                    setUsername("testuser");
                    setPassword("testpass");
                }})))
                .andExpect(status().isOk()); 
    }

    @Test
    public void givenNoToken_whenAccessSecuredEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/example"))  // TODO: Change this when we create an actual endpoint 
                .andExpect(status().isForbidden());
    }
}