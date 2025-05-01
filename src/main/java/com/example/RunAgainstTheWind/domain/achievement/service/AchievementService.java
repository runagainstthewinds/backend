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
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private ValidationService v;

    @Transactional(readOnly = true)
    public List<AchievementDTO> getUserAchievements(UUID userId) {
        v.validateUserExists(userId);
        
        return userAchievementRepository.findByUser_UserId(userId)
            .stream()
            .map(ua -> new AchievementDTO(
                ua.getAchievement().getAchievementName(),
                ua.getAchievement().getDescription(),
                ua.getDateAchieved(),
                userId
            ))
            .toList();
    }

    @Transactional
    public AchievementDTO assignAchievementToUser(UUID userId, String achievementName) {
        v.validateUserExists(userId);
        v.validateStringInput(achievementName);

        Achievement achievement = achievementRepository.findById(achievementName)
        .orElseThrow(() -> new AchievementNotFoundException("Achievement not found: " + achievementName));

        if (userAchievementRepository.existsByUser_UserIdAndAchievement_AchievementName(userId, achievement.getAchievementName())) {
            throw new AchievementAlreadyAssignedException("Achievement already assigned to user: " + achievementName);
        }

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(userRepository.getReferenceById(userId));
        userAchievement.setAchievement(achievement);
        userAchievement.setDateAchieved(LocalDate.now());
        userAchievementRepository.save(userAchievement);

        return new AchievementDTO(
            achievement.getAchievementName(),
            achievement.getDescription(),
            userAchievement.getDateAchieved(),
            userId
        );
    }
}
