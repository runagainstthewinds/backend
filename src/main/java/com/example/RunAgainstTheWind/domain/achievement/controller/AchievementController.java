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
        List<AchievementDTO> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<AchievementDTO> assignAchievementToUser(
        @PathVariable UUID userId,
        @RequestBody Map<String, Object> request
    ) {
        String achievementName = (String) request.get("achievementName");
        AchievementDTO createdAchievement = achievementService.assignAchievementToUser(userId, achievementName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAchievement);
    }
}
