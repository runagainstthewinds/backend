package com.example.RunAgainstTheWind.domain.trainingSession.service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private ValidationService v;

    public List<TrainingSessionDTO> getTrainingSessionsByUserId(UUID userId) {
        return trainingSessionRepository.getTrainingSessionsByUserId(userId);
    }

    public List<TrainingSessionDTO> getTrainingSessionsByTrainingPlanId(Long trainingPlanId) {
        return trainingSessionRepository.findByTrainingPlanId(trainingPlanId);
    }

    public TrainingSessionDTO createTrainingSession(UUID userId, TrainingSessionDTO trainingSessionDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        Optional<TrainingSession> existingSession = trainingSessionRepository.findDuplicateSession(
            user,
            trainingSessionDTO.getDate(),
            trainingSessionDTO.getAchievedDistance(),
            trainingSessionDTO.getAchievedDuration()
        );
        if (existingSession.isPresent()) return null;

        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setTrainingType(trainingSessionDTO.getTrainingType());
        trainingSession.setDate(trainingSessionDTO.getDate());
        trainingSession.setDistance(trainingSessionDTO.getDistance());
        trainingSession.setDuration(trainingSessionDTO.getDuration());
        trainingSession.setPace(trainingSessionDTO.getPace());
        trainingSession.setIsComplete(trainingSessionDTO.getIsComplete());
        trainingSession.setAchievedPace(trainingSessionDTO.getAchievedPace());
        trainingSession.setAchievedDistance(trainingSessionDTO.getAchievedDistance());
        trainingSession.setAchievedDuration(trainingSessionDTO.getAchievedDuration());
        trainingSession.setEffort(trainingSessionDTO.getEffort());
        trainingSession.setNotes(trainingSessionDTO.getNotes());

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingSessionDTO.getTrainingPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Training plan not found with id: " + trainingSessionDTO.getTrainingPlanId()));
        Shoe shoe = shoeRepository.findById(trainingSessionDTO.getShoeId())
                .orElseThrow(() -> new EntityNotFoundException("Shoe not found with id: " + trainingSessionDTO.getShoeId()));

        trainingSession.setTrainingPlan(trainingPlan);
        trainingSession.setShoe(shoe);
        trainingSession.setUser(user);
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);

        trainingSessionDTO.setTrainingSessionId(savedSession.getTrainingSessionId());
        trainingSessionDTO.setUserId(user.getUserId());
        trainingSessionDTO.setShoeId(shoe.getShoeId());
        trainingSessionDTO.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        return trainingSessionDTO;
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

        existingSession.setTrainingType(trainingSessionDTO.getTrainingType());
        existingSession.setDate(trainingSessionDTO.getDate());
        existingSession.setDistance(trainingSessionDTO.getDistance());
        existingSession.setDuration(trainingSessionDTO.getDuration());
        existingSession.setPace(trainingSessionDTO.getPace());
        existingSession.setIsComplete(trainingSessionDTO.getIsComplete());
        existingSession.setAchievedPace(trainingSessionDTO.getAchievedPace());
        existingSession.setAchievedDistance(trainingSessionDTO.getAchievedDistance());
        existingSession.setAchievedDuration(trainingSessionDTO.getAchievedDuration());
        existingSession.setEffort(trainingSessionDTO.getEffort());
        existingSession.setNotes(trainingSessionDTO.getNotes());

        Shoe shoe = shoeRepository.findById(trainingSessionDTO.getShoeId())
                .orElseThrow(() -> new EntityNotFoundException("Shoe not found with id: " + trainingSessionDTO.getShoeId()));
        existingSession.setShoe(shoe);
        TrainingSession updatedSession = trainingSessionRepository.save(existingSession);

        trainingSessionDTO.setTrainingSessionId(updatedSession.getTrainingPlan().getTrainingPlanId());
        trainingSessionDTO.setUserId(updatedSession.getUser().getUserId());
        trainingSessionDTO.setTrainingPlanId(trainingSessionId);

        return trainingSessionDTO;
    }
}
