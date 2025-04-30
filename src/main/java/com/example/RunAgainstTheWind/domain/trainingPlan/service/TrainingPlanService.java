package com.example.RunAgainstTheWind.domain.trainingPlan.service;

import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class TrainingPlanService {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private UserRepository userRepository;  

    public TrainingPlanDTO getTrainingPlanByUserId(UUID userId) {
        return trainingPlanRepository.getTrainingPlanByUserId(userId);
    }

    public TrainingPlanDTO createOrUpdateTrainingPlan(UUID userId, TrainingPlanDTO trainingPlanDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        TrainingPlan trainingPlan = user.getTrainingPlan();
        if (trainingPlan == null) {
            // Create new training plan
            trainingPlan = toEntity(trainingPlanDTO);
            trainingPlan.setTrainingPlanId(null); // Ensure ID is not set for new entity
        } else {
            // Update existing training plan
            updateEntity(trainingPlan, trainingPlanDTO);
            trainingSessionRepository.deleteAll(trainingPlan.getTrainingSessions());
            trainingPlan.getTrainingSessions().clear();
        }

        trainingPlan.setUser(user);
        user.setTrainingPlan(trainingPlan);
        TrainingPlan savedTrainingPlan = trainingPlanRepository.save(trainingPlan);

        return toDTO(savedTrainingPlan);
    }

    public void deleteTrainingPlan(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        TrainingPlan trainingPlan = user.getTrainingPlan();
        if (trainingPlan == null) {
            throw new EntityNotFoundException("Training plan not found for user id: " + userId);
        }
        trainingSessionRepository.deleteAll(trainingPlan.getTrainingSessions());
        user.setTrainingPlan(null);
        userRepository.save(user);
        trainingPlanRepository.delete(trainingPlan);
    }

    private TrainingPlanDTO toDTO(TrainingPlan trainingPlan) {
        TrainingPlanDTO dto = new TrainingPlanDTO();
        dto.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        dto.setUserId(trainingPlan.getUser().getUserId());
        dto.setStartDate(trainingPlan.getStartDate());
        dto.setEndDate(trainingPlan.getEndDate());
        dto.setPlanType(trainingPlan.getPlanType());
        dto.setRoadType(trainingPlan.getRoadType());
        dto.setGoalDistance(trainingPlan.getGoalDistance());
        dto.setGoalTime(trainingPlan.getGoalTime());
        return dto;
    }

    private TrainingPlan toEntity(TrainingPlanDTO dto) {
        TrainingPlan trainingPlan = new TrainingPlan();
        trainingPlan.setStartDate(dto.getStartDate());
        trainingPlan.setEndDate(dto.getEndDate());
        trainingPlan.setPlanType(dto.getPlanType());
        trainingPlan.setRoadType(dto.getRoadType());
        trainingPlan.setGoalDistance(dto.getGoalDistance());
        trainingPlan.setGoalTime(dto.getGoalTime());
        return trainingPlan;
    }

    private void updateEntity(TrainingPlan trainingPlan, TrainingPlanDTO dto) {
        trainingPlan.setStartDate(dto.getStartDate());
        trainingPlan.setEndDate(dto.getEndDate());
        trainingPlan.setPlanType(dto.getPlanType());
        trainingPlan.setRoadType(dto.getRoadType());
        trainingPlan.setGoalDistance(dto.getGoalDistance());
        trainingPlan.setGoalTime(dto.getGoalTime());
    }
}
