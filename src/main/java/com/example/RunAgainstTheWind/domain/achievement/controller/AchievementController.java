package com.example.RunAgainstTheWind.domain.achievement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;

@RestController
@RequestMapping("users/{userId}/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<Achievement>> getUserAchievements(@PathVariable UUID userId) {
        List<Achievement> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    @PostMapping("/{achievementId}")
    public ResponseEntity<Void> assignAchievementToUser(@PathVariable UUID userId, @PathVariable Long achievementId) {
        achievementService.assignAchievementToUser(userId, achievementId);
        return ResponseEntity.ok().build();
    }
}
