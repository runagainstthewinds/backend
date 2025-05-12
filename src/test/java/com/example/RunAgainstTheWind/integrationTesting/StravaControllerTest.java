package com.example.RunAgainstTheWind.integrationTesting;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.externalApi.strava.controller.StravaController;
import com.example.RunAgainstTheWind.externalApi.strava.model.StravaAthleteResponse;
import com.example.RunAgainstTheWind.externalApi.strava.model.StravaTokenResponse;
import com.example.RunAgainstTheWind.externalApi.strava.service.StravaService;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class StravaControllerTest {

    @Autowired
    private StravaController stravaController;

    @MockBean
    private StravaService stravaService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String testUsername = "strava_controller_test_user";
    private Principal mockPrincipal;

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
            // Update the user's Strava info
            testUser.setStravaToken("test_strava_token");
            testUser.setStravaRefreshToken("test_strava_refresh_token");
            testUser.setStravaTokenExpiresAt(Instant.now().plusSeconds(3600).getEpochSecond());
            testUser.setStravaAthleteId(12345L);
        } else {
            // Create a new test user
            testUser = new User();
            testUser.setUsername(testUsername);
            testUser.setEmail("strava_controller_test@example.com");
            testUser.setPassword("testpassword");
            testUser.setStravaToken("test_strava_token");
            testUser.setStravaRefreshToken("test_strava_refresh_token");
            testUser.setStravaTokenExpiresAt(Instant.now().plusSeconds(3600).getEpochSecond());
            testUser.setStravaAthleteId(12345L);
        }
        
        userRepository.save(testUser);

        // Create a mock principal that returns our test username
        mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(testUsername);
    }

    @Test
    public void givenAuthenticatedUser_whenGetAuthUrl_thenReturnsUrl() {
        // Arrange
        String expectedUrl = "https://strava.com/oauth/test-url";
        when(stravaService.getAuthorizationUrl(anyString())).thenReturn(expectedUrl);
        
        // Act
        ResponseEntity<Map<String, String>> response = stravaController.getAuthorizationUrl(testUsername);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().get("url"));
    }

    @Test
    public void givenValidStravaCallback_whenReceived_thenExchangesTokenAndReturnsSuccess() {
        // Arrange
        String code = "valid_auth_code";
        String state = testUsername;
        
        StravaTokenResponse tokenResponse = new StravaTokenResponse();
        tokenResponse.setAccessToken("new_access_token");
        tokenResponse.setRefreshToken("new_refresh_token");
        tokenResponse.setExpiresAt(Instant.now().plusSeconds(7200).getEpochSecond());
        
        StravaAthleteResponse athlete = new StravaAthleteResponse();
        athlete.setId(12345L);
        athlete.setFirstname("Test");
        athlete.setLastname("User");
        tokenResponse.setAthlete(athlete);
        
        when(stravaService.exchangeToken(eq(code), eq(state))).thenReturn(tokenResponse);
        
        // Act
        ResponseEntity<?> response = stravaController.stravaCallback(code, state, null);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), 
                "Response should be 200 OK, response body: " + response.getBody());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals("success", body.get("status"));
        assertEquals(state, body.get("username"));
    }

    @Test
    public void givenStravaCallbackWithError_whenReceived_thenReturnsError() {
        // Act
        ResponseEntity<?> response = stravaController.stravaCallback(null, null, "access_denied");
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        
        assertEquals("error", body.get("status"));
        assertEquals("access_denied", body.get("message"));
    }
}
