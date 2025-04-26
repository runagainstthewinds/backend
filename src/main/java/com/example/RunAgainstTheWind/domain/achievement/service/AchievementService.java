package com.example.RunAgainstTheWind.domain.achievement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.repository.UserAchievementRepository;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                             UserAchievementRepository userAchievementRepository,
                             UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
    }

    public List<Achievement> getUserAchievements(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        return userAchievementRepository.findAchievementsByUserId(userId);
    }

    public void assignAchievementToUser(UUID userId, Long achievementId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        // Validate achievement exists
        if (!achievementRepository.existsById(achievementId)) {
            throw new RuntimeException("Achievement not found");
        }
        // Check for duplicate assignment
        if (userAchievementRepository.existsUserAchievement(userId, achievementId) == 1) {
            throw new RuntimeException("Achievement already assigned to user");
        }
        // Assign achievement
        userAchievementRepository.assignAchievementToUser(userId, achievementId);
    }
}
