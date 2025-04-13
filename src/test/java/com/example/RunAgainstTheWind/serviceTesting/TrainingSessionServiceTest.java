package com.example.RunAgainstTheWind.serviceTesting;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.service.TrainingSessionService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.dto.user.UserDTO;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainingSessionService trainingSessionService;

    private UUID userId;
    private User user;
    private TrainingSession trainingSession;
    private TrainingSessionDTO trainingSessionDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("bf299756-4de6-4bc4-99d9-bd68daeb76ad");

        user = new User();
        user.setUserId(userId);
        user.setUsername("test1");
        user.setEmail("test1@gmail.com");

        trainingSession = new TrainingSession();
        trainingSession.setTrainingSessionId(1L);
        trainingSession.setDate(new Date());
        trainingSession.setDistance(10000.0);
        trainingSession.setDuration(60.0);
        trainingSession.setGoalPace(6.0);
        trainingSession.setIsCompleted(false);
        trainingSession.setEffort(4);
        trainingSession.setTrainingType(TrainingType.LONG_RUN);
        trainingSession.setUser(user);

        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainingSessionId(1L);
        trainingSessionDTO.setDate(new Date());
        trainingSessionDTO.setDistance(10000.0);
        trainingSessionDTO.setDuration(60.0);
        trainingSessionDTO.setGoalPace(6.0);
        trainingSessionDTO.setIsCompleted(false);
        trainingSessionDTO.setEffort(4);
        trainingSessionDTO.setTrainingType(TrainingType.LONG_RUN);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setUsername("test1");
        userDTO.setEmail("test1@gmail.com");
        trainingSessionDTO.setUser(userDTO);
    }

    @Test
    void getTrainingSessionsByUserId_Success_ReturnsListOfDTOs() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trainingSessionRepository.findByUser(user)).thenReturn(List.of(trainingSession));

        // Act
        List<TrainingSessionDTO> result = trainingSessionService.getTrainingSessionsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        TrainingSessionDTO dto = result.get(0);
        assertEquals(trainingSession.getTrainingSessionId(), dto.getTrainingSessionId());
        assertEquals(trainingSession.getDistance(), dto.getDistance());
        assertEquals(trainingSession.getTrainingType(), dto.getTrainingType());
        assertEquals(userId, dto.getUser().getUserId());
        verify(userRepository).findById(userId);
        verify(trainingSessionRepository).findByUser(user);
    }

    @Test
    void getTrainingSessionsByUserId_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                trainingSessionService.getTrainingSessionsByUserId(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(trainingSessionRepository);
    }

    @Test
    void createTrainingSession_Success_ReturnsCreatedDTO() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        TrainingSessionDTO result = trainingSessionService.createTrainingSession(userId, trainingSessionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(trainingSessionDTO.getTrainingSessionId(), result.getTrainingSessionId());
        assertEquals(trainingSessionDTO.getDistance(), result.getDistance());
        assertEquals(trainingSessionDTO.getTrainingType(), result.getTrainingType());
        assertEquals(userId, result.getUser().getUserId());
        verify(userRepository).findById(userId);
        verify(trainingSessionRepository).save(any(TrainingSession.class));
    }

    @Test
    void createTrainingSession_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                trainingSessionService.createTrainingSession(userId, trainingSessionDTO));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(trainingSessionRepository);
    }

    @Test
    void deleteTrainingSession_Success_DeletesSession() {
        // Arrange
        Long sessionId = 1L;
        when(trainingSessionRepository.existsById(sessionId)).thenReturn(true);

        // Act
        trainingSessionService.deleteTrainingSession(sessionId);

        // Assert
        verify(trainingSessionRepository).existsById(sessionId);
        verify(trainingSessionRepository).deleteById(sessionId);
    }

    @Test
    void deleteTrainingSession_SessionNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long sessionId = 1L;
        when(trainingSessionRepository.existsById(sessionId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                trainingSessionService.deleteTrainingSession(sessionId));
        assertEquals("Training session not found with id: " + sessionId, exception.getMessage());
        verify(trainingSessionRepository).existsById(sessionId);
        verify(trainingSessionRepository, never()).deleteById(sessionId);
    }

    @Test
    void updateTrainingSession_Success_ReturnsUpdatedDTO() {
        // Arrange
        Long sessionId = 1L;
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        TrainingSessionDTO result = trainingSessionService.updateTrainingSession(sessionId, trainingSessionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(trainingSessionDTO.getTrainingSessionId(), result.getTrainingSessionId());
        assertEquals(trainingSessionDTO.getDistance(), result.getDistance());
        assertEquals(trainingSessionDTO.getTrainingType(), result.getTrainingType());
        assertEquals(userId, result.getUser().getUserId());
        verify(trainingSessionRepository).findById(sessionId);
        verify(trainingSessionRepository).save(any(TrainingSession.class));
    }

    @Test
    void updateTrainingSession_SessionNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long sessionId = 1L;
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                trainingSessionService.updateTrainingSession(sessionId, trainingSessionDTO));
        assertEquals("Training session not found with id: " + sessionId, exception.getMessage());
        verify(trainingSessionRepository).findById(sessionId);
        verify(trainingSessionRepository, never()).save(any());
    }
}