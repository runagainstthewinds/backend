package com.example.RunAgainstTheWind.domain.achievement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.model.UserAchievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.repository.UserAchievementRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;
import com.example.RunAgainstTheWind.exceptions.achievement.AchievementAlreadyAssignedException;
import com.example.RunAgainstTheWind.exceptions.achievement.AchievementNotFoundException;

@Service
public class AchievementService {
    
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private ValidationService v;

    @Transactional(readOnly = true)
    public List<AchievementDTO> getUserAchievements(UUID userId) {
        v.validateUserExists(userId);
        
        return achievementRepository.findAchievementsByUserId(userId);
    }

    @Transactional
    public AchievementDTO assignAchievementToUser(UUID userId, Integer achievementId) {
        User user = v.validateUserExistsAndReturn(userId);
        v.validateIntegerInput(achievementId);

        Achievement achievement = achievementRepository.findById(achievementId)
        .orElseThrow(() -> new AchievementNotFoundException("Achievement not found: " + achievementId));

        if (userAchievementRepository.existsByUser_UserIdAndAchievement_AchievementId(userId, achievement.getAchievementId())) {
            throw new AchievementAlreadyAssignedException("Achievement already assigned to user: " + achievementId);
        }

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievement.setDateAchieved(LocalDate.now());
        userAchievementRepository.save(userAchievement);

        return new AchievementDTO(
            achievement.getAchievementId(),
            achievement.getAchievementName(),
            achievement.getDescription(),
            userAchievement.getDateAchieved(),
            userId
        );
    }
}
