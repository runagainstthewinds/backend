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
        
        if (!achievementRepository.existsByAchievementId(1)) {
            achievementRepository.save(new Achievement(1, "First Run", "Completed your first run"));
        }

        if (!achievementRepository.existsByAchievementId(2)) {
            achievementRepository.save(new Achievement(2, "Early Bird", "Completed a run before 6 AM"));
        }

        if (!achievementRepository.existsByAchievementId(3)) {
            achievementRepository.save(new Achievement(3, "Rain Runner", "Completed a run in the rain"));
        }

        if (!achievementRepository.existsByAchievementId(4)) {
            achievementRepository.save(new Achievement(4, "Marathon Finisher", "Completed a full marathon"));
        }

        if (!achievementRepository.existsByAchievementId(5)) {
            achievementRepository.save(new Achievement(5, "Streak Master", "Run for 7 consecutive days"));
        }

        if (!achievementRepository.existsByAchievementId(6)) {
            achievementRepository.save(new Achievement(6, "Trail Explorer", "Completed a trail run"));
        }

        if (!achievementRepository.existsByAchievementId(7)) {
            achievementRepository.save(new Achievement(7, "Speed Demon", "Ran 5K under 20 minutes"));
        }

        if (!achievementRepository.existsByAchievementId(8)) {
            achievementRepository.save(new Achievement(8, "Globe Trotter", "Run in 5 different cities"));
        }

        if (!achievementRepository.existsByAchievementId(9)) {
            achievementRepository.save(new Achievement(9, "Consistency King", "Run 20 times in a month"));
        }
    }
}