package com.example.RunAgainstTheWind.domain.trainingPlan.service;

import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.exceptions.trainingPlan.ActiveTrainingPlanExistsException;
import com.example.RunAgainstTheWind.algorithm.TrainingPlanCreator;
import com.example.RunAgainstTheWind.algorithm.TrainingPlanSkeleton;
import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.service.TrainingSessionService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class TrainingPlanService {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;  

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Autowired
    private ValidationService v;

    @Transactional(readOnly = true)
    public List<TrainingPlanDTO> getTrainingPlanByUserId(UUID userId) {
        return trainingPlanRepository.getTrainingPlanByUserId(userId);
    }

    public TrainingPlanDTO getCurrentTrainingPlanByUserId(UUID userId) {
        v.validateUserExistsAndReturn(userId);
        return trainingPlanRepository.getCurrentTrainingPlanByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("No active training plan found for user with id: " + userId));
    }

    public Map<String, Object> createTrainingPlan(UUID userId, TrainingPlanDTO trainingPlanDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        if (trainingPlanRepository.getCurrentTrainingPlanByUserId(userId).isPresent()) {
            throw new ActiveTrainingPlanExistsException("An active training plan already exists for this user.");
        }

        // Convert goal distance from kilometers to meters for storage
        Double goalDistanceMeters = trainingPlanDTO.getGoalDistance() * 1000.0;

        TrainingPlan trainingPlan = new TrainingPlan(
            trainingPlanDTO.getPlanName(),
            trainingPlanDTO.getStartDate(),
            trainingPlanDTO.getEndDate(),
            goalDistanceMeters,
            trainingPlanDTO.getDifficulty(),
            false,
            user
        );

        // Save the training plan first to get an ID
        TrainingPlan savedTrainingPlan = trainingPlanRepository.save(trainingPlan);
        trainingPlanDTO.setTrainingPlanId(savedTrainingPlan.getTrainingPlanId());
        trainingPlanDTO.setUserId(userId);

        TrainingPlanCreator planCreator = new TrainingPlanCreator(
            trainingSessionService.getTrainingSessionsByUserId(userId),
            trainingPlanDTO.getDifficulty(),
            trainingPlanDTO.getStartDate(),
            trainingPlanDTO.getEndDate(),
            trainingPlanDTO.getGoalDistance(),
            savedTrainingPlan  // Use the saved plan with ID
        );

        TrainingPlanSkeleton trainingPlanSkeleton = planCreator.getTrainingPlanSkeleton();
        
        // List to store all created sessions
        List<TrainingSessionDTO> createdSessions = new ArrayList<>();
        
        // Save all training sessions from the skeleton
        for (List<TrainingSession> weekSessions : trainingPlanSkeleton.getPlan().values()) {
            for (TrainingSession session : weekSessions) {
                // Convert TrainingSession to TrainingSessionDTO
                TrainingSessionDTO sessionDTO = new TrainingSessionDTO(
                    session.getTrainingType(),
                    session.getDate(),
                    session.getDistance(),
                    session.getDuration(),
                    session.getPace(),
                    session.getIsComplete(),
                    session.getAchievedDistance(),
                    session.getAchievedDuration(),
                    session.getAchievedPace(),
                    session.getEffort(),
                    session.getNotes(),
                    savedTrainingPlan.getTrainingPlanId(),  // Use the saved plan's ID
                    null,  
                    userId
                );
                
                TrainingSessionDTO createdSession = trainingSessionService.createTrainingSession(userId, sessionDTO);
                if (createdSession != null) {
                    createdSessions.add(createdSession);
                }
            }
        }
     
        // Return both the training plan and sessions as separate entities
        Map<String, Object> response = new HashMap<>();
        response.put("trainingPlan", trainingPlanDTO);
        response.put("trainingSessions", createdSessions);
        return response;
    }

    @Transactional
    public void deleteTrainingPlan(Long trainingPlanId) {
        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingPlanId)
            .orElseThrow(() -> new EntityNotFoundException("Training plan not found with id: " + trainingPlanId));
        trainingPlanRepository.delete(trainingPlan);
    }
}
