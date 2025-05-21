package com.example.RunAgainstTheWind.config;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.model.AchievementEnum;
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
        // Initialize all achievements from the enum
        for (AchievementEnum achievementEnum : AchievementEnum.values()) {
            if (!achievementRepository.existsByAchievementId(achievementEnum.getId())) {
                achievementRepository.save(new Achievement(
                    achievementEnum.getId(),
                    achievementEnum.getName(),
                    achievementEnum.getDescription()
                ));
            }
        }
    }
}
