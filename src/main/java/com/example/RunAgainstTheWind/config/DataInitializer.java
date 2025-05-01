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
        
        if (!achievementRepository.existsByAchievementName("achievement1")) {
            achievementRepository.save(new Achievement("first_5k", "First achievement for completing a milestone."));
        }

        if (!achievementRepository.existsByAchievementName("achievement2")) {
            achievementRepository.save(new Achievement("achievement2", "Second achievement for consistent progress."));
        }

        if (!achievementRepository.existsByAchievementName("achievement3")) {
            achievementRepository.save(new Achievement("achievement3", "Third achievement for outstanding performance."));
        }

        if (!achievementRepository.existsByAchievementName("first_1k")) {
            achievementRepository.save(new Achievement("first_1k", "Completed your first 1K run."));
        }

        if (!achievementRepository.existsByAchievementName("first_5k")) {
            achievementRepository.save(new Achievement("first_5k", "Completed your first 5K run."));
        }

        if (!achievementRepository.existsByAchievementName("first_10k")) {
            achievementRepository.save(new Achievement("first_10k", "Completed your first 10K run."));
        }

        if (!achievementRepository.existsByAchievementName("half_marathon")) {
            achievementRepository.save(new Achievement("half_marathon", "Completed your first half marathon."));
        }

        if (!achievementRepository.existsByAchievementName("full_marathon")) {
            achievementRepository.save(new Achievement("full_marathon", "Completed your first full marathon."));
        }

        if (!achievementRepository.existsByAchievementName("fast_5k")) {
            achievementRepository.save(new Achievement("fast_5k", "Ran a 5K in under 25 minutes."));
        }

        if (!achievementRepository.existsByAchievementName("fast_10k")) {
            achievementRepository.save(new Achievement("fast_10k", "Ran a 10K in under 50 minutes."));
        }

        if (!achievementRepository.existsByAchievementName("consistent_10k_30")) {
            achievementRepository.save(new Achievement("consistent_10k_30", "Ran 10K, 30 times."));
        }

        if (!achievementRepository.existsByAchievementName("consistent_5k_50")) {
            achievementRepository.save(new Achievement("consistent_5k_50", "Ran 5K, 50 times."));
        }

        if (!achievementRepository.existsByAchievementName("long_distance_100")) {
            achievementRepository.save(new Achievement("long_distance_100", "Ran more than 100K total."));
        }
    }
}