package com.example.RunAgainstTheWind.config;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AchievementRepository achievementRepository;

    @Override
    public void run(String... args) throws Exception {
        
        if (!achievementRepository.existsByAchievementId(AchievementConfig.FIRST_RUN_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.FIRST_RUN_ID, 
                AchievementConfig.FIRST_RUN_NAME, 
                AchievementConfig.FIRST_RUN_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.EARLY_BIRD_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.EARLY_BIRD_ID, 
                AchievementConfig.EARLY_BIRD_NAME, 
                AchievementConfig.EARLY_BIRD_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.RAIN_RUNNER_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.RAIN_RUNNER_ID, 
                AchievementConfig.RAIN_RUNNER_NAME, 
                AchievementConfig.RAIN_RUNNER_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.MARATHON_FINISHER_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.MARATHON_FINISHER_ID, 
                AchievementConfig.MARATHON_FINISHER_NAME, 
                AchievementConfig.MARATHON_FINISHER_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.STREAK_MASTER_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.STREAK_MASTER_ID, 
                AchievementConfig.STREAK_MASTER_NAME, 
                AchievementConfig.STREAK_MASTER_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.TRAIL_EXPLORER_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.TRAIL_EXPLORER_ID, 
                AchievementConfig.TRAIL_EXPLORER_NAME, 
                AchievementConfig.TRAIL_EXPLORER_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.SPEED_DEMON_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.SPEED_DEMON_ID, 
                AchievementConfig.SPEED_DEMON_NAME, 
                AchievementConfig.SPEED_DEMON_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.GLOBE_TROTTER_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.GLOBE_TROTTER_ID, 
                AchievementConfig.GLOBE_TROTTER_NAME, 
                AchievementConfig.GLOBE_TROTTER_DESC
            ));
        }

        if (!achievementRepository.existsByAchievementId(AchievementConfig.CONSISTENCY_KING_ID)) {
            achievementRepository.save(new Achievement(
                AchievementConfig.CONSISTENCY_KING_ID, 
                AchievementConfig.CONSISTENCY_KING_NAME, 
                AchievementConfig.CONSISTENCY_KING_DESC
            ));
        }
    }
}