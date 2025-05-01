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
        
        if (!achievementRepository.existsByAchievementName("First Run")) {
            achievementRepository.save(new Achievement("First Run", "Completed your first run"));
        }

        if (!achievementRepository.existsByAchievementName("Early Bird")) {
            achievementRepository.save(new Achievement("Early Bird", "Completed a run before 6 AM"));
        }

        if (!achievementRepository.existsByAchievementName("Rain Runner")) {
            achievementRepository.save(new Achievement("Rain Runner", "Completed a run in the rain"));
        }

        if (!achievementRepository.existsByAchievementName("Marathon Finisher")) {
            achievementRepository.save(new Achievement("Marathon Finisher", "Completed a full marathon"));
        }

        if (!achievementRepository.existsByAchievementName("Streak Master")) {
            achievementRepository.save(new Achievement("Streak Master", "Run for 7 consecutive days"));
        }

        if (!achievementRepository.existsByAchievementName("Trail Explorer")) {
            achievementRepository.save(new Achievement("Trail Explorer", "Completed a trail run"));
        }

        if (!achievementRepository.existsByAchievementName("Speed Demon")) {
            achievementRepository.save(new Achievement("Speed Demon", "Ran 5K under 20 minutes"));
        }

        if (!achievementRepository.existsByAchievementName("Globe Trotter")) {
            achievementRepository.save(new Achievement("Globe Trotter", "Run in 5 different cities"));
        }

        if (!achievementRepository.existsByAchievementName("Consistency King")) {
            achievementRepository.save(new Achievement("Consistency King", "Run 20 times in a month"));
        }
    }
}