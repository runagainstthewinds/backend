package com.example.RunAgainstTheWind.serviceTesting;

import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingPlan.service.TrainingPlanService;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.enumeration.Road;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainingPlanService trainingPlanService;

    private UUID userId;
    private User user;
    private TrainingPlan trainingPlan;
    private TrainingSession trainingSession;
    private TrainingPlanDTO trainingPlanDTO;
    private TrainingSessionDTO trainingSessionDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("bf299756-4de6-4bc4-99d9-bd68daeb76ad");

        user = new User();
        user.setUserId(userId);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        trainingPlan = new TrainingPlan();
        trainingPlan.setTrainingPlanId(1L);
        trainingPlan.setStartDate(new Date());
        trainingPlan.setEndDate(new Date());
        trainingPlan.setPlanType("Marathon");
        trainingPlan.setRoadType(Road.ASPHALT);
        trainingPlan.setGoalDistance(42.2);
        trainingPlan.setGoalTime(240.0);
        trainingPlan.setUser(user);

        trainingSession = new TrainingSession();
        trainingSession.setTrainingSessionId(1L);
        trainingSession.setDate(new Date());
        trainingSession.setDistance(10000.0);
        trainingSession.setDuration(60.0);
        trainingSession.setGoalPace(6.0);
        trainingSession.setIsCompleted(false);
        trainingSession.setTrainingType(TrainingType.LONG_RUN);
        trainingSession.setUser(user);
        trainingSession.setTrainingPlan(trainingPlan);
        trainingPlan.setTrainingSessions(new ArrayList<>(Arrays.asList(trainingSession)));

        trainingPlanDTO = new TrainingPlanDTO();
        trainingPlanDTO.setTrainingPlanId(null); // New plan
        trainingPlanDTO.setUserId(userId);
        trainingPlanDTO.setStartDate(new Date());
        trainingPlanDTO.setEndDate(new Date());
        trainingPlanDTO.setPlanType("Marathon");
        trainingPlanDTO.setRoadType(Road.ASPHALT);
        trainingPlanDTO.setGoalDistance(42.2);
        trainingPlanDTO.setGoalTime(240.0);

        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainingSessionId(null); // New session
        trainingSessionDTO.setUserId(userId);
        trainingSessionDTO.setDate(new Date());
        trainingSessionDTO.setDistance(10000.0);
        trainingSessionDTO.setDuration(60.0);
        trainingSessionDTO.setGoalPace(6.0);
        trainingSessionDTO.setIsCompleted(false);
        trainingSessionDTO.setTrainingType(TrainingType.LONG_RUN);

        trainingPlanDTO.setTrainingSessions(Arrays.asList(trainingSessionDTO));
    }

    @Test
    void getTrainingPlanByUserId_SuccessWithPlan_ReturnsDTO() {
        // Arrange
        user.setTrainingPlan(trainingPlan);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        TrainingPlanDTO result = trainingPlanService.getTrainingPlanByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(trainingPlan.getTrainingPlanId(), result.getTrainingPlanId());
        assertEquals(userId, result.getUserId());
        assertEquals(trainingPlan.getPlanType(), result.getPlanType());
        assertEquals(trainingPlan.getRoadType(), result.getRoadType());
        assertEquals(trainingPlan.getGoalDistance(), result.getGoalDistance());
        assertEquals(1, result.getTrainingSessions().size());
        TrainingSessionDTO sessionDTO = result.getTrainingSessions().get(0);
        assertEquals(trainingSession.getTrainingSessionId(), sessionDTO.getTrainingSessionId());
        assertEquals(trainingSession.getDistance(), sessionDTO.getDistance());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getTrainingPlanByUserId_NoPlan_ReturnsEmptyDTO() {
        // Arrange
        user.setTrainingPlan(null);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        TrainingPlanDTO result = trainingPlanService.getTrainingPlanByUserId(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.getTrainingPlanId());
        assertEquals(userId, result.getUserId());
        assertNull(result.getPlanType());
        assertNull(result.getRoadType());
        assertNull(result.getGoalDistance());
        assertTrue(result.getTrainingSessions().isEmpty());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getTrainingPlanByUserId_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainingPlanService.getTrainingPlanByUserId(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createTrainingPlan_Success_CreatesNewPlan() {
        // Arrange
        user.setTrainingPlan(null);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(trainingPlanRepository.save(any(TrainingPlan.class))).thenReturn(trainingPlan);
        when(trainingSessionRepository.saveAll(anyList())).thenReturn(Arrays.asList(trainingSession));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        TrainingPlanDTO result = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);

        // Assert
        assertNotNull(result);
        assertEquals(trainingPlan.getTrainingPlanId(), result.getTrainingPlanId());
        assertEquals(userId, result.getUserId());
        assertEquals(trainingPlanDTO.getPlanType(), result.getPlanType());
        assertEquals(trainingPlanDTO.getRoadType(), result.getRoadType());
        assertEquals(trainingPlanDTO.getGoalDistance(), result.getGoalDistance());
        assertEquals(1, result.getTrainingSessions().size());
        TrainingSessionDTO sessionDTO = result.getTrainingSessions().get(0);
        assertEquals(trainingSession.getTrainingSessionId(), sessionDTO.getTrainingSessionId());
        verify(userRepository, times(1)).findById(userId);
        verify(trainingPlanRepository, times(1)).save(any(TrainingPlan.class));
        verify(trainingSessionRepository, times(1)).saveAll(anyList());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateTrainingPlan_Success_UpdatesExistingPlan() {
        // Arrange
        user.setTrainingPlan(trainingPlan);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(trainingPlanRepository.save(any(TrainingPlan.class))).thenReturn(trainingPlan);
        when(trainingSessionRepository.saveAll(anyList())).thenReturn(Arrays.asList(trainingSession));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        TrainingPlanDTO result = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);

        // Assert
        assertNotNull(result);
        assertEquals(trainingPlan.getTrainingPlanId(), result.getTrainingPlanId());
        assertEquals(userId, result.getUserId());
        assertEquals(trainingPlanDTO.getPlanType(), result.getPlanType());
        assertEquals(trainingPlanDTO.getRoadType(), result.getRoadType());
        assertEquals(trainingPlanDTO.getGoalDistance(), result.getGoalDistance());
        assertEquals(1, result.getTrainingSessions().size());
        verify(userRepository, times(1)).findById(userId);
        verify(trainingPlanRepository, times(1)).save(any(TrainingPlan.class));
        verify(trainingSessionRepository, times(1)).deleteAll(anyList());
        verify(trainingSessionRepository, times(1)).saveAll(anyList());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createTrainingPlan_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(trainingPlanRepository, trainingSessionRepository);
    }

    @Test
    void deleteTrainingPlan_Success_DeletesPlanAndSessions() {
        // Arrange
        user.setTrainingPlan(trainingPlan);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        trainingPlanService.deleteTrainingPlan(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, times(1)).deleteAll(anyList());
        verify(userRepository, times(1)).save(any(User.class));
        verify(trainingPlanRepository, times(1)).delete(trainingPlan);
    }

    @Test
    void deleteTrainingPlan_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainingPlanService.deleteTrainingPlan(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(trainingPlanRepository, trainingSessionRepository);
    }

    @Test
    void deleteTrainingPlan_NoPlan_ThrowsEntityNotFoundException() {
        // Arrange
        user.setTrainingPlan(null);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainingPlanService.deleteTrainingPlan(userId));
        assertEquals("Training plan not found for user id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(trainingPlanRepository, trainingSessionRepository);
    }

    @Test
    void createTrainingPlan_EmptySessions_Success() {
        // Arrange
        trainingPlanDTO.setTrainingSessions(Collections.emptyList());
        user.setTrainingPlan(null);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(trainingPlanRepository.save(any(TrainingPlan.class))).thenReturn(trainingPlan);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        TrainingPlanDTO result = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);

        // Assert
        assertNotNull(result);
        assertEquals(trainingPlan.getTrainingPlanId(), result.getTrainingPlanId());
        assertEquals(userId, result.getUserId());
        assertEquals(trainingPlanDTO.getPlanType(), result.getPlanType());
        assertTrue(result.getTrainingSessions().isEmpty());
    }
}