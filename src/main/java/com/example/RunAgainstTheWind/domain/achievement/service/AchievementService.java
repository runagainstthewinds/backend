package com.example.RunAgainstTheWind.domain.achievement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.model.UserAchievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.repository.UserAchievementRepository;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;

@Service
public class AchievementService {
    
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AchievementDTO> getUserAchievements(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        List<Object[]> results = userAchievementRepository.findAchievementsByUserId(userId);

        return results.stream()
            .map(row -> new AchievementDTO(
                (String) row[0],
                (String) row[1],
                row[2] instanceof java.sql.Date
                    ? ((java.sql.Date) row[2]).toLocalDate()
                    : (LocalDate) row[2]
            ))
            .toList(); 
    }

    public AchievementDTO assignAchievementToUser(UUID userId, String achievementName) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        Achievement achievement = achievementRepository.findById(achievementName)
            .orElseThrow(() -> new RuntimeException("Achievement not found"));

        if (userAchievementRepository.existsUserAchievement(userId, achievementName) == 1) {
            throw new RuntimeException("Achievement already assigned to user");
        }

        try {
            userAchievementRepository.assignAchievementToUser(userId, achievementName);

            UserAchievement userAchievement = userAchievementRepository
                .findByUser_UserIdAndAchievement_AchievementName(userId, achievementName)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve inserted achievement"));

            return new AchievementDTO(
                achievement.getAchievementName(),
                achievement.getDescription(),
                userAchievement.getDateAchieved()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to assign achievement to user", e);
        }
    }
}
