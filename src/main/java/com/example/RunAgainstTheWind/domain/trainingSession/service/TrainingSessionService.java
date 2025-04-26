package com.example.RunAgainstTheWind.domain.trainingSession.service;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.dto.user.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TrainingSessionDTO> getTrainingSessionsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        List<TrainingSession> sessions = trainingSessionRepository.findByUser(user);
        return sessions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TrainingSessionDTO createTrainingSession(UUID userId, TrainingSessionDTO trainingSessionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        TrainingSession trainingSession = toEntity(trainingSessionDTO);
        trainingSession.setUser(user);
        trainingSession.setTrainingSessionId(null); // Ensure ID is not set for new entity
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);
        return toDTO(savedSession);
    }

    public void deleteTrainingSession(Long trainingSessionId) {
        if (!trainingSessionRepository.existsById(trainingSessionId)) {
            throw new EntityNotFoundException("Training session not found with id: " + trainingSessionId);
        }
        trainingSessionRepository.deleteById(trainingSessionId);
    }

    public TrainingSessionDTO updateTrainingSession(Long trainingSessionId, TrainingSessionDTO trainingSessionDTO) {
        TrainingSession existingSession = trainingSessionRepository.findById(trainingSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Training session not found with id: " + trainingSessionId));

        // Update fields
        existingSession.setDate(trainingSessionDTO.getDate());
        existingSession.setDistance(trainingSessionDTO.getDistance());
        existingSession.setDuration(trainingSessionDTO.getDuration());
        existingSession.setGoalPace(trainingSessionDTO.getGoalPace());
        existingSession.setIsCompleted(trainingSessionDTO.getIsCompleted());
        existingSession.setAchievedPace(trainingSessionDTO.getAchievedPace());
        existingSession.setAchievedDistance(trainingSessionDTO.getAchievedDistance());
        existingSession.setAchievedDuration(trainingSessionDTO.getAchievedDuration());
        existingSession.setEffort(trainingSessionDTO.getEffort());
        existingSession.setTrainingType(trainingSessionDTO.getTrainingType());

        TrainingSession updatedSession = trainingSessionRepository.save(existingSession);
        return toDTO(updatedSession);
    }

    private TrainingSessionDTO toDTO(TrainingSession trainingSession) {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setTrainingSessionId(trainingSession.getTrainingSessionId());
        dto.setUserId(trainingSession.getUser().getUserId());
        dto.setDate(trainingSession.getDate());
        dto.setDistance(trainingSession.getDistance());
        dto.setDuration(trainingSession.getDuration());
        dto.setGoalPace(trainingSession.getGoalPace());
        dto.setIsCompleted(trainingSession.getIsCompleted());
        dto.setAchievedPace(trainingSession.getAchievedPace());
        dto.setAchievedDistance(trainingSession.getAchievedDistance());
        dto.setAchievedDuration(trainingSession.getAchievedDuration());
        dto.setEffort(trainingSession.getEffort());
        dto.setTrainingType(trainingSession.getTrainingType());

        if (trainingSession.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(trainingSession.getUser().getUserId());
            userDTO.setUsername(trainingSession.getUser().getUsername());
            userDTO.setEmail(trainingSession.getUser().getEmail());
            userDTO.setUserId(trainingSession.getUser().getUserId());
        }

        return dto;
    }

    private TrainingSession toEntity(TrainingSessionDTO dto) {
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setDate(dto.getDate());
        trainingSession.setDistance(dto.getDistance());
        trainingSession.setDuration(dto.getDuration());
        trainingSession.setGoalPace(dto.getGoalPace());
        trainingSession.setIsCompleted(dto.getIsCompleted());
        trainingSession.setAchievedPace(dto.getAchievedPace());
        trainingSession.setAchievedDistance(dto.getAchievedDistance());
        trainingSession.setAchievedDuration(dto.getAchievedDuration());
        trainingSession.setEffort(dto.getEffort());
        trainingSession.setTrainingType(dto.getTrainingType());
        return trainingSession;
    }
}
