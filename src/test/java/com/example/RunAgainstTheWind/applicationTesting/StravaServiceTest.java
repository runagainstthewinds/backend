package com.example.RunAgainstTheWind.applicationTesting;

import com.example.RunAgainstTheWind.config.StravaConfig;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.externalApi.strava.service.StravaService;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StravaServiceTest {

    @Autowired
    private StravaService stravaService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StravaConfig stravaConfig;

    private User testUser;
    private String testUsername = "strava_test_user";

    @BeforeAll
    static void setupEnv() {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    @BeforeEach
    public void setup() {
        User existingUser = userRepository.findByUsername(testUsername);
        if (existingUser != null) {
            testUser = existingUser;
            testUser.setStravaToken("test_strava_token");
            testUser.setStravaRefreshToken("test_strava_refresh_token");
            testUser.setStravaTokenExpiresAt(Instant.now().plusSeconds(3600).getEpochSecond());
            testUser.setStravaAthleteId(12345L);
        } else {
            testUser = new User();
            testUser.setUsername(testUsername);
            testUser.setEmail("strava_test@example.com");
            testUser.setPassword("testpassword");
            testUser.setStravaToken("test_strava_token");
            testUser.setStravaRefreshToken("test_strava_refresh_token");
            testUser.setStravaTokenExpiresAt(Instant.now().plusSeconds(3600).getEpochSecond());
            testUser.setStravaAthleteId(12345L);
        }
        
        userRepository.save(testUser);
    }

    @Test
    public void givenUsername_whenGetAuthorizationUrl_thenReturnsUrlWithCorrectParams() {
        // Act
        String url = stravaService.getAuthorizationUrl();
        
        // Assert
        assertNotNull(url);
        assertTrue(url.contains("client_id=" + stravaConfig.getClientId()));
        assertTrue(url.contains("redirect_uri="));
        assertTrue(url.contains("scope=read,activity:read_all"));
    }

    @Test
    public void givenUsernameAndState_whenGetAuthorizationUrl_thenReturnsUrlWithState() {
        // Act
        String url = stravaService.getAuthorizationUrl(testUsername);
        
        // Assert
        assertNotNull(url);
        assertTrue(url.contains("state=" + testUsername));
    }

    @Test
    public void givenConnectedUser_whenIsConnectedToStrava_thenReturnsTrue() {
        // Act
        boolean isConnected = stravaService.isConnectedToStrava(testUsername);
        
        // Assert
        assertTrue(isConnected);
    }

    @Test
    public void givenUserWithoutStravaToken_whenIsConnectedToStrava_thenReturnsFalse() {
        // Arrange
        String noStravaUsername = "no_strava_user_" + System.currentTimeMillis(); // Unique username
        User existingUser = userRepository.findByUsername(noStravaUsername);
        
        User userWithoutStrava;
        if (existingUser != null) {
            userWithoutStrava = existingUser;
            userWithoutStrava.setStravaToken(null);
            userWithoutStrava.setStravaRefreshToken(null);
        } else {
            userWithoutStrava = new User();
            userWithoutStrava.setUsername(noStravaUsername);
            userWithoutStrava.setEmail("no_strava@example.com");
            userWithoutStrava.setPassword("password");
        }
        userRepository.save(userWithoutStrava);
        
        // Act
        boolean isConnected = stravaService.isConnectedToStrava(noStravaUsername);
        
        // Assert
        assertFalse(isConnected);
    }

    @Test
    public void givenNonExistentUser_whenIsConnectedToStrava_thenReturnsFalse() {
        // Act
        boolean isConnected = stravaService.isConnectedToStrava("non_existent_user");
        
        // Assert
        assertFalse(isConnected);
    }
}
