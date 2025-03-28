package com.example.RunAgainstTheWind.domain.achievement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.dataTransferObject.achievement.AchievementCreationDTO;
import com.example.RunAgainstTheWind.dataTransferObject.achievement.AchievementResponseDTO;
import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;

@RestController
@RequestMapping("/achievements")
public class AchievementController {
    
    @Autowired
    private AchievementService achievementService;

    // Get all achievements
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<AchievementResponseDTO> getAllAchievements() {
        List<Achievement> achievements = achievementService.getAllAchievements();
        return achievements.stream()
                .map(AchievementResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Create new achievement
    // Fields: name, description
    // NOTE: This might need to be done by an admin because the user should not be able to create new achievements himself.
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponseDTO createAchievement(@RequestBody AchievementCreationDTO achievementDTO) {
        Achievement achievement = achievementService.createAchievement(achievementDTO); 
        return new AchievementResponseDTO(achievement);
    }

    // Delete achievement by Id
    // Fields: achievementId
    // NOTE: This might need to be done by an admin because the user should not be able to delete achievements himself.
    @DeleteMapping("/{achievementId}")
    public ResponseEntity<String> deleteAchievement(@PathVariable Long achievementId) {
        achievementService.deleteAchievement(achievementId);
        return ResponseEntity 
                .ok("Achievement with ID " + achievementId + " deleted successfully");
    }
}
