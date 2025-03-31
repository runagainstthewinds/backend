package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.application.auth.JWTService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.domain.user.service.MyUserDetailsService;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

@SpringBootTest
@Transactional
public class JwtServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeAll
    static void setupEnv() {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    @BeforeEach
    public void setup() {
        // Register a test user in the database
        User user = new User();
        user.setUsername("jwtuser");
        user.setEmail("jwtemail");
        user.setPassword(passwordEncoder.encode("jwtpass"));
        userRepo.save(user);
    }

    @Test
    public void givenUsername_whenGenerateToken_thenTokenIsValid() {
        String username = "jwtuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUserName(token));
    }

    @Test
    public void givenValidToken_whenValidateToken_thenReturnsTrue() {
        String username = "jwtuser";
        String token = jwtService.generateToken(username);
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    public void givenInvalidToken_whenValidateToken_thenReturnsFalse() {
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("jwtuser");
        assertFalse(jwtService.validateToken("invalid.token.here", userDetails));
    }
}