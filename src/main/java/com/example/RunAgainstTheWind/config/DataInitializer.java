package com.example.RunAgainstTheWind.config;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AchievementRepository achievementRepository;

    public DataInitializer(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check and add achievement1
        if (!achievementRepository.existsByName("achievement1")) {
            achievementRepository.save(new Achievement("achievement1", "First achievement for completing a milestone."));
        }

        // Check and add achievement2
        if (!achievementRepository.existsByName("achievement2")) {
            achievementRepository.save(new Achievement("achievement2", "Second achievement for consistent progress."));
        }

        // Check and add achievement3
        if (!achievementRepository.existsByName("achievement3")) {
            achievementRepository.save(new Achievement("achievement3", "Third achievement for outstanding performance."));
        }
    }
}