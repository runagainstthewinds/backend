package com.example.RunAgainstTheWind.unitTesting;

import com.example.RunAgainstTheWind.dataTransferObject.achievement.AchievementCreationDTO;
import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    private AchievementCreationDTO validAchievementDTO;
    private Achievement sampleAchievement;

    @BeforeEach
    public void setUp() {
        validAchievementDTO = new AchievementCreationDTO("Test Achievement", "Test Description");
        sampleAchievement = new Achievement("Test Achievement", "Test Description");
        sampleAchievement.setAchievementId(1L);
    }

    @Test
    public void testCreateAchievement_Successful() {
        // Arrange
        when(achievementRepository.save(any(Achievement.class))).thenReturn(sampleAchievement);

        // Act
        Achievement createdAchievement = achievementService.createAchievement(validAchievementDTO);

        // Assert
        assertNotNull(createdAchievement);
        assertEquals("Test Achievement", createdAchievement.getName());
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    public void testCreateAchievement_NullInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(null);
        });
        
        verify(achievementRepository, never()).save(any(Achievement.class));
    }

    @Test
    public void testDeleteAchievement_Successful() {
        // Arrange
        when(achievementRepository.existsById(1L)).thenReturn(true);

        // Act
        achievementService.deleteAchievement(1L);

        // Assert
        verify(achievementRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteAchievement_NonExistentAchievement() {
        // Arrange
        when(achievementRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            achievementService.deleteAchievement(1L);
        });

        verify(achievementRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteAchievement_NullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.deleteAchievement(null);
        });

        verify(achievementRepository, never()).deleteById(any());
    }

    @Test
    public void testMarkAchievementAsCompleted_Successful() {
        // Arrange
        when(achievementRepository.findById(1L)).thenReturn(Optional.of(sampleAchievement));
        when(achievementRepository.save(any(Achievement.class))).thenReturn(sampleAchievement);

        // Act
        Achievement completedAchievement = achievementService.markAchievementAsCompleted(1L);

        // Assert
        assertTrue(completedAchievement.isCompleted());
        verify(achievementRepository, times(1)).findById(1L);
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    public void testMarkAchievementAsCompleted_NonExistentAchievement() {
        // Arrange
        when(achievementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            achievementService.markAchievementAsCompleted(1L);
        });

        verify(achievementRepository, times(1)).findById(1L);
        verify(achievementRepository, never()).save(any(Achievement.class));
    }

    @Test
    public void testMarkAchievementAsCompleted_NullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.markAchievementAsCompleted(null);
        });

        verify(achievementRepository, never()).findById(any());
        verify(achievementRepository, never()).save(any(Achievement.class));
    }
}
