package com.example.RunAgainstTheWind.application;

import com.example.RunAgainstTheWind.application.user.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Users testUser;

    @BeforeEach
    public void setup() throws Exception {
        testUser = new Users();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");

        // Register the test user
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidUser_whenRegister_thenReturnsUser() throws Exception {
        Users newUser = new Users();
        newUser.setId(2);
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
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
        Users invalidUser = new Users();
        invalidUser.setUsername("nonexistent");
        invalidUser.setPassword("wrongpass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fail"));
    }
}