package com.example.RunAgainstTheWind.domain.achievement.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;

@RestController
@RequestMapping("/achievements")
public class AchievementController {
    
    private final AchievementService achievementService;
    
    @Autowired
    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }
    
    /**
     * Get achievements for a specific user
     * @param userId The UUID of the user
     * @return Achievement object with user's achievements
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserAchievements(@PathVariable String userId) {
        try {
            List<String> achievement = achievementService.getUserAchievements(userId);
            return ResponseEntity.ok(achievement);
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error reading achievement data: " + e.getMessage());
        }
    }
    
    /**
     * Mark a specific achievement as achieved for a user
     * @param userId The UUID of the user
     * @param achievementNumber The achievement number to mark (1, 2, or 3)
     * @return Success or error message
     */
    @PostMapping("/{userId}/{achievementName}")
    public ResponseEntity<?> markAchievement(
            @PathVariable String userId,
            @PathVariable String achievementName) {
        try {
            boolean success = achievementService.markAchievement(userId, achievementName);
            if (success) {
                return ResponseEntity.ok("Achievement " + achievementName + " marked as achieved for user " + userId);
            } else {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update achievement");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating achievement data: " + e.getMessage());
        }
    }
}
