package com.example.RunAgainstTheWind.domain.strava.service;

import com.example.RunAgainstTheWind.config.StravaConfig;
import com.example.RunAgainstTheWind.domain.strava.model.StravaActivityResponse;
import com.example.RunAgainstTheWind.domain.strava.model.StravaTokenResponse;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StravaService {
    
    private static final Logger logger = LoggerFactory.getLogger(StravaService.class);
    
    @Autowired
    private WebClient stravaWebClient;
    
    @Autowired
    private StravaConfig stravaConfig;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Generate the authorization URL for Strava
     * @return URL that the user should be redirected to for authorization
     */
    public String getAuthorizationUrl() {
        return getAuthorizationUrl(null);
    }
    
    /**
     * Generate the authorization URL for Strava with state parameter
     * @param username Username to associate with the Strava connection
     * @return URL that the user should be redirected to for authorization
     */
    public String getAuthorizationUrl(String username) {
        StringBuilder url = new StringBuilder("https://www.strava.com/oauth/authorize" +
                "?client_id=" + stravaConfig.getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(stravaConfig.getRedirectUri(), StandardCharsets.UTF_8) +
                "&approval_prompt=force" +
                "&scope=read,activity:read_all");
        
        // Add state parameter if provided
        if (username != null && !username.isEmpty()) {
            url.append("&state=").append(URLEncoder.encode(username, StandardCharsets.UTF_8));
        }
        
        logger.info("Generated Strava auth URL: {}", url);
        return url.toString();
    }
    
    /**
     * Exchange authorization code for tokens
     * @param code Authorization code received from Strava
     * @param username Username of the authenticated user
     * @return Response with tokens
     */
    public StravaTokenResponse exchangeToken(String code, String username) {
        logger.info("Exchanging token for user: {}, code: {}", username, code);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new IllegalArgumentException("User not found");
        }
        
        // Create WebClient for this specific request
        WebClient client = WebClient.builder().build();
        
        // Create form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", stravaConfig.getClientId());
        formData.add("client_secret", stravaConfig.getClientSecret());
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        
        logger.info("Sending token exchange request to Strava with client_id: {}", stravaConfig.getClientId());
        
        try {
            StravaTokenResponse tokenResponse = client.post()
                    .uri("https://www.strava.com/oauth/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(StravaTokenResponse.class)
                    .block();
            
            logger.info("Successfully received token response for user: {}", username);
            
            // Save tokens and athlete info to user
            user.setStravaToken(tokenResponse.getAccessToken());
            user.setStravaRefreshToken(tokenResponse.getRefreshToken());
            user.setStravaTokenExpiresAt(tokenResponse.getExpiresAt());
            if (tokenResponse.getAthlete() != null) {
                user.setStravaAthleteId(tokenResponse.getAthlete().getId());
            }
            userRepository.save(user);
            
            return tokenResponse;
        } catch (Exception e) {
            logger.error("Error exchanging token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to exchange Strava token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if token needs to be refreshed and refresh if necessary
     * @param user User to check token for
     * @return true if token was refreshed, false otherwise
     */
    private boolean refreshTokenIfNeeded(User user) {
        if (user.getStravaToken() == null || user.getStravaRefreshToken() == null) {
            logger.error("User not connected to Strava: {}", user.getUsername());
            throw new IllegalStateException("User not connected to Strava");
        }
        
        // Check if token is expired or will expire in the next 10 minutes
        long currentTime = Instant.now().getEpochSecond();
        long buffer = 10 * 60; // 10 minutes in seconds
        
        if (user.getStravaTokenExpiresAt() == null || currentTime + buffer >= user.getStravaTokenExpiresAt()) {
            logger.info("Token expired or will expire soon for user: {}. Refreshing...", user.getUsername());
            try {
                refreshToken(user);
                return true;
            } catch (Exception e) {
                logger.error("Failed to refresh token: {}", e.getMessage(), e);
                throw new RuntimeException("Unable to refresh Strava token", e);
            }
        }
        
        return false;
    }
    
    /**
     * Refresh access token using the refresh token
     * @param user User to refresh token for
     * @return Updated token response
     */
    private StravaTokenResponse refreshToken(User user) {
        logger.info("Refreshing token for user: {}", user.getUsername());
        
        // Create form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", stravaConfig.getClientId());
        formData.add("client_secret", stravaConfig.getClientSecret());
        formData.add("refresh_token", user.getStravaRefreshToken());
        formData.add("grant_type", "refresh_token");
        
        WebClient client = WebClient.builder().build();
        
        try {
            StravaTokenResponse tokenResponse = client.post()
                    .uri("https://www.strava.com/oauth/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(StravaTokenResponse.class)
                    .block();
            
            // Update user with new tokens
            user.setStravaToken(tokenResponse.getAccessToken());
            user.setStravaRefreshToken(tokenResponse.getRefreshToken());
            user.setStravaTokenExpiresAt(tokenResponse.getExpiresAt());
            userRepository.save(user);
            
            logger.info("Successfully refreshed token for user: {}", user.getUsername());
            return tokenResponse;
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh Strava token", e);
        }
    }
    
    /**
     * Get athlete's activities from Strava
     * @param username Username of the authenticated user
     * @param page Page number for pagination
     * @param perPage Number of items per page
     * @return List of activities
     */
    public List<StravaActivityResponse> getAthleteActivities(String username, int page, int perPage) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Refresh token if needed
        refreshTokenIfNeeded(user);
        
        try {
            return stravaWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/athlete/activities")
                            .queryParam("page", page)
                            .queryParam("per_page", perPage)
                            .build())
                    .header("Authorization", "Bearer " + user.getStravaToken())
                    .retrieve()
                    .bodyToMono(StravaActivityResponse[].class)
                    .map(Arrays::asList)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Force token refresh and retry
                logger.info("Received 401 from Strava, forcing token refresh for user: {}", username);
                refreshToken(user);
                
                // Retry with new token
                return stravaWebClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/athlete/activities")
                                .queryParam("page", page)
                                .queryParam("per_page", perPage)
                                .build())
                        .header("Authorization", "Bearer " + user.getStravaToken())
                        .retrieve()
                        .bodyToMono(StravaActivityResponse[].class)
                        .map(Arrays::asList)
                        .block();
            } else {
                logger.error("Error getting activities: {}", e.getMessage(), e);
                throw e;
            }
        }
    }
    
    /**
     * Get detailed information about a specific activity
     * @param username Username of the authenticated user
     * @param activityId ID of the activity to retrieve
     * @return Activity details
     */
    public StravaActivityResponse getActivity(String username, Long activityId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Refresh token if needed
        refreshTokenIfNeeded(user);
        
        try {
            return stravaWebClient
                    .get()
                    .uri("/activities/" + activityId)
                    .header("Authorization", "Bearer " + user.getStravaToken())
                    .retrieve()
                    .bodyToMono(StravaActivityResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Force token refresh and retry
                refreshToken(user);
                
                // Retry with new token
                return stravaWebClient
                        .get()
                        .uri("/activities/" + activityId)
                        .header("Authorization", "Bearer " + user.getStravaToken())
                        .retrieve()
                        .bodyToMono(StravaActivityResponse.class)
                        .block();
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Get athlete statistics from Strava
     * @param username Username of the authenticated user
     * @return Map of statistics
     */
    public Map<String, Object> getAthleteStats(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Refresh token if needed
        refreshTokenIfNeeded(user);
        
        // Use stored athlete ID if available, otherwise get it
        Long athleteId = user.getStravaAthleteId();
        if (athleteId == null) {
            athleteId = getAthleteId(user.getStravaToken());
            user.setStravaAthleteId(athleteId);
            userRepository.save(user);
        }
        
        try {
            return stravaWebClient
                    .get()
                    .uri("/athletes/" + athleteId + "/stats")
                    .header("Authorization", "Bearer " + user.getStravaToken())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Force token refresh and retry
                refreshToken(user);
                
                // Retry with new token
                return stravaWebClient
                        .get()
                        .uri("/athletes/" + athleteId + "/stats")
                        .header("Authorization", "Bearer " + user.getStravaToken())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Check if a user is connected to Strava
     * @param username Username to check
     * @return true if connected, false otherwise
     */
    public boolean isConnectedToStrava(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        return user.getStravaToken() != null && user.getStravaRefreshToken() != null;
    }
    
    /**
     * Get athlete ID from Strava
     * @param accessToken Strava access token
     * @return Athlete ID
     */
    private Long getAthleteId(String accessToken) {
        Map<String, Object> athlete = stravaWebClient
                .get()
                .uri("/athlete")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        
        return Long.valueOf(athlete.get("id").toString());
    }
}