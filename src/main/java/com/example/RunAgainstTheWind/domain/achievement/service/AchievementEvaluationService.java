package com.example.RunAgainstTheWind.domain.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.config.AchievementConfig;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementEvaluationService {
    
    private static final LocalTime EARLY_BIRD_THRESHOLD = LocalTime.of(6, 0);
    private static final double SPEED_DEMON_MIN_DISTANCE = 5.0; // 5km
    private static final double SPEED_DEMON_MAX_PACE = 4.0; // 4:00 min/km

    @Autowired
    private ValidationService v;

    @Autowired
    private AchievementService achievementService;

    public void evaluateRunningAchievements(UUID userId, TrainingSessionDTO trainingSession) {
        v.validateUserExists(userId);
        v.validateTrainingSession(trainingSession);
        
        List<AchievementDTO> existingAchievements = achievementService.getUserAchievements(userId);
        
        if (!hasAchievement(existingAchievements, AchievementConfig.FIRST_RUN_NAME) && checkFirstRunAchievement(userId)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.FIRST_RUN_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.EARLY_BIRD_NAME) && checkEarlyBirdAchievement(userId, trainingSession.getDate())) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.EARLY_BIRD_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.RAIN_RUNNER_NAME) && checkRainRunnerAchievement(userId, trainingSession)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.RAIN_RUNNER_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.MARATHON_FINISHER_NAME) && checkMarathonFinisherAchievement(userId, trainingSession.getAchievedDistance())) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.MARATHON_FINISHER_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.STREAK_MASTER_NAME) && checkStreakMasterAchievement(userId, trainingSession)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.STREAK_MASTER_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.TRAIL_EXPLORER_NAME) && checkTrailExplorerAchievement(userId, trainingSession)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.TRAIL_EXPLORER_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.SPEED_DEMON_NAME) && checkSpeedDemonAchievement(userId, trainingSession.getAchievedDistance(), trainingSession.getAchievedPace())) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.SPEED_DEMON_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.GLOBE_TROTTER_NAME) && checkGlobeTrotterAchievement(userId, trainingSession)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.GLOBE_TROTTER_ID);
        }
        if (!hasAchievement(existingAchievements, AchievementConfig.CONSISTENCY_KING_NAME) && checkConsistencyKingAchievement(userId, trainingSession)) {
            achievementService.assignAchievementToUser(userId, AchievementConfig.CONSISTENCY_KING_ID);
        }
    }

    private boolean hasAchievement(List<AchievementDTO> achievements, String achievementName) {
        return achievements.stream()
                .anyMatch(achievement -> achievement.getAchievementName().equals(achievementName));
    }

    private boolean checkFirstRunAchievement(UUID userId) {
        return true;
    }

    private boolean checkMarathonFinisherAchievement(UUID userId, double distance) {
        return distance >= 42.0;
    }

    private boolean checkEarlyBirdAchievement(UUID userId, LocalDate date) {
        return date != null && date.atTime(LocalTime.now()).toLocalTime().isBefore(EARLY_BIRD_THRESHOLD);
    }

    private boolean checkSpeedDemonAchievement(UUID userId, double distance, Double pace) {
        return pace != null && distance >= SPEED_DEMON_MIN_DISTANCE && pace <= SPEED_DEMON_MAX_PACE;
    }

    private boolean checkRainRunnerAchievement(UUID userId, TrainingSessionDTO trainingSession) {
        // TODO: Implement rain detection logic
        // This will check if the run was completed during rainy weather
        return false;
    }

    private boolean checkStreakMasterAchievement(UUID userId, TrainingSessionDTO trainingSession) {
        // TODO: Implement streak tracking logic
        // This will check for consecutive days of running
        return false;
    }

    private boolean checkTrailExplorerAchievement(UUID userId, TrainingSessionDTO trainingSession) {
        // TODO: Implement trail running detection logic
        // This will check if the run was on a trail
        return false;
    }

    private boolean checkGlobeTrotterAchievement(UUID userId, TrainingSessionDTO trainingSession) {
        // TODO: Implement location tracking logic
        // This will check for runs in different locations
        return false;
    }

    private boolean checkConsistencyKingAchievement(UUID userId, TrainingSessionDTO trainingSession) {
        // TODO: Implement consistency tracking logic
        // This will check for regular running patterns
        return false;
    }
}
