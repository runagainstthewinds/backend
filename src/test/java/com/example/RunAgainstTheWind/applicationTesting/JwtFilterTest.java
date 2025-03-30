package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setup() throws Exception {
        User user = new User();
        user.setUsername("jwtuser");
        user.setEmail("jwtemail");
        user.setPassword("jwtpass");

        // Register user
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Login to get token
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        jwtToken = "Bearer " + result.andReturn().getResponse().getContentAsString();
    }

    @Test
    public void givenValidToken_whenAccessSecuredEndpoint_thenOk() throws Exception {
        mockMvc.perform(get("/example") // TODO: Change this when we create an actual endpoint 
                .header("Authorization", jwtToken))
                .andExpect(status().isOk()); 
    }

    @Test
    public void givenInvalidToken_whenAccessSecuredEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/example")  // TODO: Change this when we create an actual endpoint 
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }
}