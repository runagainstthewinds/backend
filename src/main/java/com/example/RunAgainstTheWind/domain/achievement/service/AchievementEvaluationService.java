package com.example.RunAgainstTheWind.domain.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.config.AchievementConfig.Achievement;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class AchievementEvaluationService {
    
    private static final LocalTime EARLY_BIRD_THRESHOLD = LocalTime.of(6, 0);
    private static final double SPEED_DEMON_MIN_DISTANCE = 5.0; // 5km
    private static final double SPEED_DEMON_MAX_PACE = 4.0; // 4:00 min/km

    @Autowired
    private ValidationService v;

    @Autowired
    private AchievementService achievementService;

    private final Map<Achievement, BiFunction<UUID, TrainingSessionDTO, Boolean>> achievementChecks;

    public AchievementEvaluationService() {
        this.achievementChecks = Map.of(
            Achievement.FIRST_RUN, (userId, session) -> checkFirstRunAchievement(userId),
            Achievement.EARLY_BIRD, (userId, session) -> checkEarlyBirdAchievement(userId, session.getDate()),
            Achievement.RAIN_RUNNER, (userId, session) -> checkRainRunnerAchievement(userId, session),
            Achievement.MARATHON_FINISHER, (userId, session) -> checkMarathonFinisherAchievement(userId, session.getAchievedDistance()),
            Achievement.STREAK_MASTER, (userId, session) -> checkStreakMasterAchievement(userId, session),
            Achievement.TRAIL_EXPLORER, (userId, session) -> checkTrailExplorerAchievement(userId, session),
            Achievement.SPEED_DEMON, (userId, session) -> checkSpeedDemonAchievement(userId, session.getAchievedDistance(), session.getAchievedPace()),
            Achievement.GLOBE_TROTTER, (userId, session) -> checkGlobeTrotterAchievement(userId, session),
            Achievement.CONSISTENCY_KING, (userId, session) -> checkConsistencyKingAchievement(userId, session)
        );
    }

    public void evaluateRunningAchievements(UUID userId, TrainingSessionDTO trainingSession) {
        v.validateUserExists(userId);
        v.validateTrainingSession(trainingSession);
        
        List<AchievementDTO> existingAchievements = achievementService.getUserAchievements(userId);
        
        for (Achievement achievement : Achievement.values()) {
            if (!hasAchievement(existingAchievements, achievement.getName()) && 
                achievementChecks.get(achievement).apply(userId, trainingSession)) {
                achievementService.assignAchievementToUser(userId, achievement.getId());
            }
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
