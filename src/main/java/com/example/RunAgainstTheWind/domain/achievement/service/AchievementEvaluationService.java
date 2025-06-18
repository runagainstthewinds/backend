package com.example.RunAgainstTheWind.domain.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.achievement.model.AchievementEnum;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class AchievementEvaluationService {
    
    private static final LocalTime EARLY_BIRD_THRESHOLD = LocalTime.of(6, 0);
    private static final LocalTime NIGHT_OWL_THRESHOLD = LocalTime.of(21, 0);
    private static final double FIVE_KM_DISTANCE = 5.0;
    private static final double TEN_KM_DISTANCE = 10.0;
    private static final double HALF_MARATHON_DISTANCE = 21.1;
    private static final double MARATHON_DISTANCE = 42.2;

    @Autowired
    private ValidationService v;

    @Autowired
    private AchievementService achievementService;

    private final Map<AchievementEnum, BiFunction<UUID, TrainingSessionDTO, Boolean>> achievementChecks;

    public AchievementEvaluationService() {
        Map<AchievementEnum, BiFunction<UUID, TrainingSessionDTO, Boolean>> checks = new EnumMap<>(AchievementEnum.class);
        
        for (AchievementEnum achievement : AchievementEnum.values()) {
            checks.put(achievement, switch (achievement) {
                case FIRST_RUN -> (userId, session) -> checkFirstRunAchievement(userId);
                case EARLY_BIRD -> (userId, session) -> checkEarlyBirdAchievement(userId, session.getDate());
                case NIGHT_OWL -> (userId, session) -> checkNightOwlAchievement(userId, session.getDate());
                case FIVE_KM -> (userId, session) -> checkFiveKmAchievement(userId, session.getAchievedDistance());
                case TEN_KM -> (userId, session) -> checkTenKmAchievement(userId, session.getAchievedDistance());
                case HALF_MARATHON -> (userId, session) -> checkHalfMarathonAchievement(userId, session.getAchievedDistance());
                case MARATHON -> (userId, session) -> checkMarathonAchievement(userId, session.getAchievedDistance());
            });
        }
        
        this.achievementChecks = checks;
    }

    public void evaluateRunningAchievements(UUID userId, TrainingSessionDTO trainingSession) {
        v.validateUserExists(userId);
        v.validateTrainingSession(trainingSession);
        
        List<AchievementDTO> existingAchievements = achievementService.getUserAchievements(userId);
        
        for (AchievementEnum achievement : AchievementEnum.values()) {
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

    private boolean checkEarlyBirdAchievement(UUID userId, LocalDate date) {
        return date != null && date.atTime(LocalTime.now()).toLocalTime().isBefore(EARLY_BIRD_THRESHOLD);
    }

    private boolean checkNightOwlAchievement(UUID userId, LocalDate date) {
        return date != null && date.atTime(LocalTime.now()).toLocalTime().isAfter(NIGHT_OWL_THRESHOLD);
    }

    private boolean checkFiveKmAchievement(UUID userId, double distance) {
        return distance >= FIVE_KM_DISTANCE;
    }

    private boolean checkTenKmAchievement(UUID userId, double distance) {
        return distance >= TEN_KM_DISTANCE;
    }

    private boolean checkHalfMarathonAchievement(UUID userId, double distance) {
        return distance >= HALF_MARATHON_DISTANCE;
    }

    private boolean checkMarathonAchievement(UUID userId, double distance) {
        return distance >= MARATHON_DISTANCE;
    }
}
