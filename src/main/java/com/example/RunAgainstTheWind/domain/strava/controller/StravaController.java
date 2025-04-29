package com.example.RunAgainstTheWind.domain.strava.controller;

import com.example.RunAgainstTheWind.domain.strava.model.StravaActivityResponse;
import com.example.RunAgainstTheWind.domain.strava.model.StravaTokenResponse;
import com.example.RunAgainstTheWind.domain.strava.service.StravaService;
import com.example.RunAgainstTheWind.domain.trainingSession.service.TrainingSessionService;
import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/strava")
public class StravaController {
    
    private static final Logger logger = LoggerFactory.getLogger(StravaController.class);
    
    @Autowired
    private StravaService stravaService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TrainingSessionService trainingSessionService;
    
    /**
     * Check if current user is connected to Strava
     * @param principal Currently authenticated user
     * @return Connection status
     */
    @GetMapping("/connection-status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        boolean isConnected = stravaService.isConnectedToStrava(principal.getName());
        response.put("connected", isConnected);
        
        if (!isConnected) {
            // Include auth URL for convenience
            response.put("authUrl", stravaService.getAuthorizationUrl());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get the authorization URL for Strava
     * @param username Username to associate with the Strava connection
     * @return URL for authorization
     */
    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(
            @RequestParam(required = true) String username) {
        // Use state parameter to store username
        String url = stravaService.getAuthorizationUrl(username);
        return ResponseEntity.ok(Map.of("url", url));
    }
    
    /**
     * Callback endpoint for Strava OAuth redirect with automatic token exchange
     * This endpoint is publicly accessible (no authentication required)
     * @param code Authorization code from Strava
     * @param state State parameter (contains username)
     * @param error Error message if authorization failed
     * @return Success message with exchange results or error message
     */
    @GetMapping("/callback")
    public ResponseEntity<?> stravaCallback(
            @RequestParam(required = false) String code, 
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        // If there's an error or code is missing
        if (error != null || code == null) {
            logger.error("Strava authorization failed: {}", error);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", error != null ? error : "No authorization code received");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate state parameter (contains username in this implementation)
        String username = state;
        if (username == null || username.isEmpty()) {
            logger.error("Invalid state parameter in Strava callback");
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid state parameter. Make sure to include username in the auth-url request.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Automatically exchange the code for tokens
            StravaTokenResponse tokenResponse = stravaService.exchangeToken(code, username);
            logger.info("Successfully connected Strava for user: {}", username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Successfully connected Strava account");
            response.put("username", username);
            
            // Don't return the actual tokens for security reasons
            response.put("athleteId", tokenResponse.getAthlete().getId());
            response.put("athleteName", tokenResponse.getAthlete().getFirstname() + " " + tokenResponse.getAthlete().getLastname());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error exchanging token: {}", e.getMessage(), e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to exchange token: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Exchange authorization code for tokens
     * @param code Authorization code from Strava
     * @param principal Currently authenticated user
     * @return Token response
     */
    @PostMapping("/exchange-token")
    public ResponseEntity<?> exchangeToken(@RequestParam String code, Principal principal) {
        logger.info("Exchanging token with code: {}, user: {}", code, principal.getName());
        try {
            StravaTokenResponse response = stravaService.exchangeToken(code, principal.getName());
            logger.info("Successfully exchanged token for user: {}", principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error exchanging token: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to exchange token");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Exchange authorization code for tokens (testing endpoint)
     * This endpoint allows direct testing with Postman without authentication
     * IMPORTANT: Remove or secure this endpoint in production!
     * @param code Authorization code from Strava
     * @param username Username to associate the Strava account with
     * @return Token response
     */
    @PostMapping("/test/exchange-token")
    public ResponseEntity<?> testExchangeToken(
            @RequestParam String code, 
            @RequestParam String username) {
        logger.info("Test exchanging token with code: {}, username: {}", code, username);
        try {
            StravaTokenResponse response = stravaService.exchangeToken(code, username);
            logger.info("Successfully exchanged token for test user: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error exchanging token in test endpoint: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to exchange token");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get athlete's activities from Strava
     * @param page Page number for pagination (default: 1)
     * @param perPage Number of items per page (default: 30)
     * @param principal Currently authenticated user
     * @return List of activities
     */
    @GetMapping("/activities/users/{userID}")
    public ResponseEntity<?> getActivities(
            @PathVariable String userID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int perPage,
            Principal principal) {
        try {
            List<StravaActivityResponse> activities = stravaService.getAthleteActivities(
                    principal.getName(), page, perPage);
            List<TrainingSessionDTO> trainingSessionDTOs = parseActivitiesToTrainingSessionDTOs(activities);
            for (TrainingSessionDTO dto : trainingSessionDTOs) {
                trainingSessionService.createTrainingSession(UUID.fromString(userID), dto); 
            }
            return ResponseEntity.ok(trainingSessionDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Not connected to Strava",
                    "authUrl", stravaService.getAuthorizationUrl()));
        } catch (Exception e) {
            logger.error("Error getting activities: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve activities"));
        }
    }
    
    /**
     * Get detailed information about a specific activity
     * @param activityId ID of the activity to retrieve
     * @param principal Currently authenticated user
     * @return Activity details
     */
    @GetMapping("/activities/{activityId}")
    public ResponseEntity<?> getActivity(
            @PathVariable Long activityId,
            Principal principal) {
        try {
            StravaActivityResponse activity = stravaService.getActivity(principal.getName(), activityId);
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Not connected to Strava",
                    "authUrl", stravaService.getAuthorizationUrl()));
        } catch (Exception e) {
            logger.error("Error getting activity: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve activity"));
        }
    }
    
    /**
     * Get athlete statistics from Strava
     * @param principal Currently authenticated user
     * @return Map of statistics
     */
    @GetMapping("/stats/{userID}")
    public ResponseEntity<?> getAthleteStats(@PathVariable String userID, Principal principal) {
        try {
            Map<String, Object> stats = stravaService.getAthleteStats(principal.getName());
            ObjectMapper objectMapper = new ObjectMapper();
            String statsJson = objectMapper.writeValueAsString(stats);
            UserDetailsDTO userDetailsDTO = parseStatsToUserDetailsDTO(statsJson);
            UserDetailsDTO createdDetails = userDetailsService.createUserDetails(UUID.fromString(userID), userDetailsDTO);
            return ResponseEntity.ok(createdDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Not connected to Strava",
                    "authUrl", stravaService.getAuthorizationUrl()));
        } catch (Exception e) {
            logger.error("Error getting stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve statistics"));
        }
    }

    /**
     * Helper method to parse Strava all_run_totals into UserDetailsDTO
     * @param statsJson String representation of the Strava stats JSON
     * @return UserDetailsDTO containing parsed run data
     */
    private UserDetailsDTO parseStatsToUserDetailsDTO(String statsJson) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(statsJson);
            JsonNode allRunTotalsNode = rootNode.get("all_run_totals");

            if (allRunTotalsNode != null) {
                userDetailsDTO.setRunCount(allRunTotalsNode.get("count").asInt());
                userDetailsDTO.setTotalDistance(allRunTotalsNode.get("distance").asDouble() / 1000);
                userDetailsDTO.setTotalDuration(allRunTotalsNode.get("moving_time").asDouble() / 60);
                userDetailsDTO.setWeeklyDistance(0.0); // As per the requirement
            }
        } catch (IOException e) {
            logger.error("Error parsing Strava stats JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse Strava stats JSON", e);
        }
        return userDetailsDTO;
    }

    /**
     * Convert Strava activities to TrainingSessionDTOs directly
     * @param activities List of Strava activities
     * @return List of TrainingSessionDTOs
     */
    private List<TrainingSessionDTO> parseActivitiesToTrainingSessionDTOs(List<StravaActivityResponse> activities) {
        List<TrainingSessionDTO> trainingSessionDTOs = new ArrayList<>();
        
        for (StravaActivityResponse activity : activities) {
            TrainingSessionDTO dto = new TrainingSessionDTO();
            
            dto.setAchievedDistance(activity.getDistance().doubleValue());
            dto.setAchievedDuration(activity.getMovingTime() / 60.0); // Convert seconds to minutes
            dto.setIsCompleted(true);
            
            if (activity.getDistance() > 0 && activity.getMovingTime() > 0) {
                // (seconds / meters) * 1000 meters/km / 60 seconds/minute = minutes/km
                dto.setAchievedPace((activity.getMovingTime() / activity.getDistance().doubleValue()) * 1000 / 60);
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dto.setDate(java.sql.Date.valueOf(sdf.format(Date.from(activity.getStartDate()))));

            dto.setTrainingSessionId(null);
            dto.setUserId(null);
            dto.setGoalPace(null);
            dto.setEffort(null);
            dto.setTrainingType(null);
            
            trainingSessionDTOs.add(dto);
        }
        
        return trainingSessionDTOs;
    }
}