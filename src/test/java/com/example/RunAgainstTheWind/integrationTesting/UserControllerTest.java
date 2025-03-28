package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUser testUser;

    @BeforeEach
    public void setup() throws Exception {
        testUser = new AppUser();
        testUser.setUsername("testuser");
        testUser.setEmail("testemail");
        testUser.setPassword("password123");

        // Register the test user
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidUser_whenRegister_thenReturnsUser() throws Exception {
        AppUser newUser = new AppUser();
        newUser.setUsername("newuser");
        newUser.setEmail("newemail");
        newUser.setPassword("newpass");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.password").isNotEmpty()); // Cannot check password value because it is encrypted
    }

    @Test
    public void givenValidCredentials_whenLogin_thenReturnsToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(not("Fail")));
    }

    @Test
    public void givenInvalidCredentials_whenLogin_thenReturnsFail() throws Exception {
        AppUser invalidUser = new AppUser();
        invalidUser.setUsername("nonexistent");
        invalidUser.setPassword("wrongpass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fail"));
    }
}