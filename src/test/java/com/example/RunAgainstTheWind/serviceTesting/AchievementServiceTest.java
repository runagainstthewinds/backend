package com.example.RunAgainstTheWind.serviceTesting;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.achievement.repository.AchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.repository.UserAchievementRepository;
import com.example.RunAgainstTheWind.domain.achievement.service.AchievementService;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementService achievementService;

    private UUID userId;
    private Long achievementId;
    private Achievement achievement1;
    private Achievement achievement2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        achievementId = 1L;

        // Initialize sample achievements
        achievement1 = new Achievement();
        achievement1.setAchievementId(1L);
        achievement1.setName("First Run");
        achievement1.setDescription("Complete your first run");

        achievement2 = new Achievement();
        achievement2.setAchievementId(2L);
        achievement2.setName("5K Runner");
        achievement2.setDescription("Run 5 kilometers");
    }

    // Tests for getUserAchievements
    @Test
    void getUserAchievements_UserExists_ReturnsAchievements() {
        // Arrange
        List<Achievement> expectedAchievements = Arrays.asList(achievement1, achievement2);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userAchievementRepository.findAchievementsByUserId(userId)).thenReturn(expectedAchievements);

        // Act
        List<Achievement> result = achievementService.getUserAchievements(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAchievements, result);
        verify(userRepository).existsById(userId);
        verify(userAchievementRepository).findAchievementsByUserId(userId);
    }

    @Test
    void getUserAchievements_UserDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.getUserAchievements(userId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userAchievementRepository, never()).findAchievementsByUserId(any());
    }

    // Tests for assignAchievementToUser
    @Test
    void assignAchievementToUser_ValidInput_AssignsAchievement() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        when(achievementRepository.existsById(achievementId)).thenReturn(true);
        when(userAchievementRepository.existsUserAchievement(userId, achievementId)).thenReturn(0);

        // Act
        achievementService.assignAchievementToUser(userId, achievementId);

        // Assert
        verify(userRepository).existsById(userId);
        verify(achievementRepository).existsById(achievementId);
        verify(userAchievementRepository).existsUserAchievement(userId, achievementId);
        verify(userAchievementRepository).assignAchievementToUser(userId, achievementId);
    }

    @Test
    void assignAchievementToUser_UserDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.assignAchievementToUser(userId, achievementId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(achievementRepository, never()).existsById(any());
        verify(userAchievementRepository, never()).existsUserAchievement(any(), any());
        verify(userAchievementRepository, never()).assignAchievementToUser(any(), any());
    }

    @Test
    void assignAchievementToUser_AchievementDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        when(achievementRepository.existsById(achievementId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.assignAchievementToUser(userId, achievementId);
        });
        assertEquals("Achievement not found", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(achievementRepository).existsById(achievementId);
        verify(userAchievementRepository, never()).existsUserAchievement(any(), any());
        verify(userAchievementRepository, never()).assignAchievementToUser(any(), any());
    }

    @Test
    void assignAchievementToUser_AchievementAlreadyAssigned_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        when(achievementRepository.existsById(achievementId)).thenReturn(true);
        when(userAchievementRepository.existsUserAchievement(userId, achievementId)).thenReturn(1);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.assignAchievementToUser(userId, achievementId);
        });
        assertEquals("Achievement already assigned to user", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(achievementRepository).existsById(achievementId);
        verify(userAchievementRepository).existsUserAchievement(userId, achievementId);
        verify(userAchievementRepository, never()).assignAchievementToUser(any(), any());
    }
}
