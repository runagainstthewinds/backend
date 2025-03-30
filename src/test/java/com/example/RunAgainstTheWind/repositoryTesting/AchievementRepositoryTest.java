package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AchievementRepositoryTest {
    
    @Autowired
    private AchievementRepository achievementRepository;

    @BeforeEach
    public void setUp() {
        achievementRepository.deleteAll();
    }

    @Test
    public void testCreateAchievement() {
        // Create a new achievement
        Achievement achievement = new Achievement("First 5k", "You ran your first 5k!");
        Achievement savedAchievement = achievementRepository.save(achievement);
        
        // Check it exists
        assertNotNull(savedAchievement.getAchievementId());

        // Check it is retrievable from databse
        Optional<Achievement> retrievedAchievement = achievementRepository.findById(savedAchievement.getAchievementId());
        assertNotNull(retrievedAchievement);

        // Check the attributes are correct
        assertNotNull(retrievedAchievement.get().getName());
        assertNotNull(retrievedAchievement.get().getDescription());
        assertEquals("First 5k", retrievedAchievement.get().getName());
        assertEquals("You ran your first 5k!", retrievedAchievement.get().getDescription());
    }
}
