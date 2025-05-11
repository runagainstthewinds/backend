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
import com.example.RunAgainstTheWind.domain.shoe.service.ShoeService;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ShoeService shoeService;

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getTrainingSessionsByUserId(UUID userId) {
        return trainingSessionRepository.getTrainingSessionsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getTrainingSessionsByTrainingPlanId(Long trainingPlanId) {
        return trainingSessionRepository.findByTrainingPlanId(trainingPlanId);
    }

    @Transactional 
    public TrainingSessionDTO createTrainingSession(UUID userId, TrainingSessionDTO trainingSessionDTO) {
        User user = v.validateUserExistsAndReturn(userId);
        
        // Check if the training session already exists for the user
        Optional<TrainingSession> existingSession = trainingSessionRepository.findDuplicateSession(
            user,
            trainingSessionDTO.getDate(),
            trainingSessionDTO.getAchievedDistance(),
            trainingSessionDTO.getAchievedDuration()
        );
        if (existingSession.isPresent()) return null;

        // Find the training plan if it exists
        TrainingPlan trainingPlan = null;
        Long trainingPlanId = trainingSessionDTO.getTrainingPlanId();
        if (trainingPlanId != null) {
            trainingPlan = trainingPlanRepository.findById(trainingPlanId)
                .orElseThrow(() -> new EntityNotFoundException("Training plan not found with id: " + trainingPlanId));
        }

        // Find the shoe if it exists
        Shoe shoe = null;
        Long shoeId = trainingSessionDTO.getShoeId();
        if (shoeId != null) {
            shoe = shoeRepository.findById(shoeId)
                .orElseThrow(() -> new EntityNotFoundException("Shoe not found with id: " + shoeId));
        }

        TrainingSession trainingSession = new TrainingSession(
            trainingSessionDTO.getTrainingType(),
            trainingSessionDTO.getDate(),
            trainingSessionDTO.getDistance(),
            trainingSessionDTO.getDuration(),
            trainingSessionDTO.getPace(),
            trainingSessionDTO.getIsComplete(),
            trainingSessionDTO.getAchievedDistance(),
            trainingSessionDTO.getAchievedDuration(),
            trainingSessionDTO.getAchievedPace(),
            trainingSessionDTO.getEffort(),
            trainingSessionDTO.getNotes(),
            trainingPlan,
            shoe,
            user
        );
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);

        trainingSessionDTO.setTrainingSessionId(savedSession.getTrainingSessionId());
        trainingSessionDTO.setTrainingPlanId(trainingPlan != null ? trainingPlan.getTrainingPlanId() : null);
        trainingSessionDTO.setShoeId(shoe != null ? shoe.getShoeId() : null);
        trainingSessionDTO.setUserId(user.getUserId());
        return trainingSessionDTO;
    }

    @Transactional
    public void deleteTrainingSession(Long trainingSessionId) {
        if (!trainingSessionRepository.existsById(trainingSessionId)) {
            throw new EntityNotFoundException("Training session not found with id: " + trainingSessionId);
        }
        trainingSessionRepository.deleteById(trainingSessionId);
    }

    @Transactional
    public TrainingSessionDTO updateTrainingSession(Long trainingSessionId, TrainingSessionDTO trainingSessionDTO) {
        TrainingSession existingSession = trainingSessionRepository.findById(trainingSessionId)
            .orElseThrow(() -> new EntityNotFoundException("Training session not found with id: " + trainingSessionId));

        Shoe shoe = null;
        if (trainingSessionDTO.getShoeId() != null) {
            shoe = shoeRepository.findById(trainingSessionDTO.getShoeId())
                .orElseThrow(() -> new EntityNotFoundException("Shoe not found with id: " + trainingSessionDTO.getShoeId()));
        }

        // Update the shoe mileage
        if (shoe != null && trainingSessionDTO.getAchievedDistance() != null && existingSession.getAchievedDistance() == null) {
            Double currentMileage = shoe.getTotalMileage() != null ? shoe.getTotalMileage() : 0.0;
            Double achievedDistance = trainingSessionDTO.getAchievedDistance();
            Double newMileage = currentMileage + achievedDistance;
            ShoeDTO mileageUpdate = new ShoeDTO();
            mileageUpdate.setTotalMileage(newMileage);
            shoeService.updateShoe(shoe.getShoeId(), mileageUpdate);
        }

        if (trainingSessionDTO.getTrainingType() != null) existingSession.setTrainingType(trainingSessionDTO.getTrainingType());
        if (trainingSessionDTO.getDate() != null) existingSession.setDate(trainingSessionDTO.getDate());
        if (trainingSessionDTO.getDistance() != null) existingSession.setDistance(trainingSessionDTO.getDistance());
        if (trainingSessionDTO.getDuration() != null) existingSession.setDuration(trainingSessionDTO.getDuration());
        if (trainingSessionDTO.getIsComplete() != null) existingSession.setIsComplete(trainingSessionDTO.getIsComplete());
        if (trainingSessionDTO.getAchievedDistance() != null) existingSession.setAchievedDistance(trainingSessionDTO.getAchievedDistance());
        if (trainingSessionDTO.getAchievedDuration() != null) existingSession.setAchievedDuration(trainingSessionDTO.getAchievedDuration());
        if (trainingSessionDTO.getEffort() != null) existingSession.setEffort(trainingSessionDTO.getEffort());
        if (trainingSessionDTO.getNotes() != null) existingSession.setNotes(trainingSessionDTO.getNotes());
        if (shoe != null) existingSession.setShoe(shoe);

        // Calculate achievedPace if both achievedDistance and achievedDuration are present
        if (trainingSessionDTO.getAchievedDistance() != null && trainingSessionDTO.getAchievedDuration() != null) {
            Double achievedPace = trainingSessionDTO.getAchievedDuration() /trainingSessionDTO.getAchievedDistance();
            existingSession.setAchievedPace(achievedPace);
        }

        TrainingSession updatedSession = trainingSessionRepository.save(existingSession);

        // Set all attributes in the response DTO
        trainingSessionDTO.setTrainingSessionId(updatedSession.getTrainingSessionId());
        trainingSessionDTO.setTrainingType(updatedSession.getTrainingType());
        trainingSessionDTO.setDate(updatedSession.getDate());
        trainingSessionDTO.setDistance(updatedSession.getDistance());
        trainingSessionDTO.setDuration(updatedSession.getDuration());
        trainingSessionDTO.setPace(updatedSession.getPace());
        trainingSessionDTO.setIsComplete(updatedSession.getIsComplete());
        trainingSessionDTO.setAchievedDistance(updatedSession.getAchievedDistance());
        trainingSessionDTO.setAchievedDuration(updatedSession.getAchievedDuration());
        trainingSessionDTO.setAchievedPace(updatedSession.getAchievedPace());
        trainingSessionDTO.setEffort(updatedSession.getEffort());
        trainingSessionDTO.setNotes(updatedSession.getNotes());
        trainingSessionDTO.setTrainingPlanId(updatedSession.getTrainingPlan() != null ? updatedSession.getTrainingPlan().getTrainingPlanId() : null);
        trainingSessionDTO.setShoeId(updatedSession.getShoe() != null ? updatedSession.getShoe().getShoeId() : null);
        trainingSessionDTO.setUserId(updatedSession.getUser().getUserId());

        return trainingSessionDTO;
    }
}
