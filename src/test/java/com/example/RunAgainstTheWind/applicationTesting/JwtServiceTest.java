package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.application.auth.JWTService;
import com.example.RunAgainstTheWind.application.user.model.Users;
import com.example.RunAgainstTheWind.application.user.repository.UserRepo;
import com.example.RunAgainstTheWind.domain.appUser.service.MyUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Register a test user in the database
        Users user = new Users();
        user.setId(1);
        user.setUsername("jwtuser");
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