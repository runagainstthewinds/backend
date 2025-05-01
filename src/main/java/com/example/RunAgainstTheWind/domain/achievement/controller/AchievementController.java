package com.example.RunAgainstTheWind.domain.achievement.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(@PathVariable UUID userId) {
        try {
            List<AchievementDTO> achievements = achievementService.getUserAchievements(userId);
            return ResponseEntity.ok(achievements);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(List.of());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> assignAchievementToUser(
        @PathVariable UUID userId,
        @RequestBody Map<String, Object> request
    ) {
        try {
            String achievementName = (String) request.get("achievementName");
            AchievementDTO createdAchievement = achievementService.assignAchievementToUser(userId, achievementName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAchievement);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
}
