package com.example.RunAgainstTheWind.domain.trainingPlan.service;

import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class TrainingPlanService {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;  

    @Autowired
    private ValidationService v;

    @Transactional(readOnly = true)
    public List<TrainingPlanDTO> getTrainingPlanByUserId(UUID userId) {
        return trainingPlanRepository.getTrainingPlanByUserId(userId);
    }

    @Transactional
    public TrainingPlanDTO createTrainingPlan(UUID userId, TrainingPlanDTO trainingPlanDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        TrainingPlan trainingPlan = new TrainingPlan(
                trainingPlanDTO.getPlanName(),
                trainingPlanDTO.getStartDate(),
                trainingPlanDTO.getEndDate(),
                trainingPlanDTO.getGoalDistance(),
                trainingPlanDTO.getGoalTime(),
                false,
                user
        );
        TrainingPlan savedTrainingPlan = trainingPlanRepository.save(trainingPlan);

        trainingPlanDTO.setTrainingPlanId(savedTrainingPlan.getTrainingPlanId());
        trainingPlanDTO.setComplete(false);
        trainingPlanDTO.setUserId(userId);
        return trainingPlanDTO;
    }

    @Transactional
    public void deleteTrainingPlan(Long trainingPlanId) {
        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingPlanId)
            .orElseThrow(() -> new EntityNotFoundException("Training plan not found with id: " + trainingPlanId));
        trainingPlanRepository.delete(trainingPlan);
    }
}
