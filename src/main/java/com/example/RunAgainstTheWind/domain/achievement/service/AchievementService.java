package com.example.RunAgainstTheWind.domain.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.dataTransferObject.achievement.AchievementCreationDTO;
import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/*
 * This class is responsible for handling the business logic related to achievements.
 * 1. Create a new achievement.
 * 2. Delete an achievement.
 * 3. Mark achievement as completed.
 */
@Service
public class AchievementService {
    
    @Autowired
    private AchievementRepository achievementRepository;

    @Transactional
    public Achievement createAchievement(@Valid AchievementCreationDTO achievementDTO) {

        // Validate the input
        if (achievementDTO == null) {
            throw new IllegalArgumentException("Achievement cannot be null");
        }

        Achievement achievement = new Achievement(achievementDTO.getName(), achievementDTO.getDescription());
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void deleteAchievement(Long achievementId) {

        // Validate the input
        if (achievementId == null) {
            throw new IllegalArgumentException("Achievement ID cannot be null");
        }
        if (!achievementRepository.existsById(achievementId)) {
            throw new EntityNotFoundException("Achievement with ID " + achievementId + " not found");
        }

        achievementRepository.deleteById(achievementId);
    }

    @Transactional
    public Achievement markAchievementAsCompleted(Long achievementId) {

        // Validate the input
        if (achievementId == null) {
            throw new IllegalArgumentException("Achievement ID cannot be null");
        }
        
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new EntityNotFoundException("Achievement with ID " + achievementId + " not found"));
        
        achievement.setCompleted(true);
        return achievementRepository.save(achievement);
    }
}
