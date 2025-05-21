package com.example.RunAgainstTheWind.domain.trainingSession.service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.achievement.service.AchievementEvaluationService;
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
import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

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

    @Autowired
    private AchievementEvaluationService achievementEvaluationService;

    @Autowired
    private UserDetailsService userDetailsService;

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

        // Calculate pace values using helper
        Double pace = calculatePace(trainingSessionDTO.getDuration(), trainingSessionDTO.getDistance());
        Double achievedPace = calculatePace(trainingSessionDTO.getAchievedDuration(), trainingSessionDTO.getAchievedDistance());

        TrainingSession trainingSession = new TrainingSession(
            trainingSessionDTO.getTrainingType(),
            trainingSessionDTO.getDate(),
            trainingSessionDTO.getDistance(),
            trainingSessionDTO.getDuration(),
            pace,
            trainingSessionDTO.getIsComplete(),
            trainingSessionDTO.getAchievedDistance(),
            trainingSessionDTO.getAchievedDuration(),
            achievedPace,
            trainingSessionDTO.getEffort(),
            trainingSessionDTO.getNotes(),
            trainingPlan,
            shoe,
            user
        );
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);

        // Update user details if achieved distance and duration are present
        if (trainingSessionDTO.getAchievedDistance() != null && trainingSessionDTO.getAchievedDuration() != null) {
            updateUserDetails(userId, trainingSessionDTO.getAchievedDistance(), trainingSessionDTO.getAchievedDuration(), false);
        }

        trainingSessionDTO.setTrainingSessionId(savedSession.getTrainingSessionId());
        trainingSessionDTO.setTrainingPlanId(trainingPlan != null ? trainingPlan.getTrainingPlanId() : null);
        trainingSessionDTO.setShoeId(shoe != null ? shoe.getShoeId() : null);
        trainingSessionDTO.setUserId(user.getUserId());
        trainingSessionDTO.setPace(savedSession.getPace());
        trainingSessionDTO.setAchievedPace(savedSession.getAchievedPace());

        if (Boolean.TRUE.equals(trainingSessionDTO.getIsComplete())) {
            achievementEvaluationService.evaluateRunningAchievements(userId, trainingSessionDTO);
        }

        return trainingSessionDTO;
    }

    @Transactional
    public void deleteTrainingSession(Long trainingSessionId) {
        TrainingSession session = trainingSessionRepository.findById(trainingSessionId)
            .orElseThrow(() -> new EntityNotFoundException("Training session not found with id: " + trainingSessionId));
        
        // Update user details if achieved distance and duration are present
        if (session.getAchievedDistance() != null && session.getAchievedDuration() != null) {
            updateUserDetails(session.getUser().getUserId(), session.getAchievedDistance(), session.getAchievedDuration(), true);
        }
        
        trainingSessionRepository.deleteById(trainingSessionId);
    }

    @Transactional
    public TrainingSessionDTO updateTrainingSession(Long trainingSessionId, TrainingSessionDTO trainingSessionDTO) {
        TrainingSession existingSession = trainingSessionRepository.findById(trainingSessionId)
            .orElseThrow(() -> new EntityNotFoundException("Training session not found with id: " + trainingSessionId));

        // If there are existing achieved values, subtract them from user details
        if (existingSession.getAchievedDistance() != null && existingSession.getAchievedDuration() != null) {
            updateUserDetails(existingSession.getUser().getUserId(), 
                existingSession.getAchievedDistance(), 
                existingSession.getAchievedDuration(), 
                true);
        }

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

        // Calculate pace values using helper
        Double pace = calculatePace(existingSession.getDuration(), existingSession.getDistance());
        existingSession.setPace(pace);
        Double achievedPace = calculatePace(existingSession.getAchievedDuration(), existingSession.getAchievedDistance());
        existingSession.setAchievedPace(achievedPace);

        TrainingSession updatedSession = trainingSessionRepository.save(existingSession);

        // Update user details with new achieved values
        if (trainingSessionDTO.getAchievedDistance() != null && trainingSessionDTO.getAchievedDuration() != null) {
            updateUserDetails(existingSession.getUser().getUserId(), 
                trainingSessionDTO.getAchievedDistance(), 
                trainingSessionDTO.getAchievedDuration(), 
                false);
        }

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

        if (Boolean.TRUE.equals(trainingSessionDTO.getIsComplete())) {
            achievementEvaluationService.evaluateRunningAchievements(trainingSessionDTO.getUserId(), trainingSessionDTO);
        }

        return trainingSessionDTO;
    }

    private void updateUserDetails(UUID userId, Double achievedDistance, Double achievedDuration, boolean isDeletion) {
        UserDetailsDTO userDetails = userDetailsService.getUserDetailsById(userId);
        if (userDetails == null) {
            userDetails = new UserDetailsDTO();
            userDetails.setTotalDistance(0.0);
            userDetails.setTotalDuration(0.0);
            userDetails.setRunCount(0);
        }

        double multiplier = isDeletion ? -1 : 1;
        
        // Add to existing values instead of replacing them
        Double currentDistance = userDetails.getTotalDistance() != null ? userDetails.getTotalDistance() : 0.0;
        Double currentDuration = userDetails.getTotalDuration() != null ? userDetails.getTotalDuration() : 0.0;
        Integer currentRunCount = userDetails.getRunCount() != null ? userDetails.getRunCount() : 0;

        userDetails.setTotalDistance(currentDistance + (achievedDistance * multiplier));
        userDetails.setTotalDuration(currentDuration + (achievedDuration * multiplier));
        userDetails.setRunCount(currentRunCount + (int)(1 * multiplier));

        userDetailsService.updateUserDetails(userId, userDetails);
    }

    private Double calculatePace(Double duration, Double distance) {
        if (distance != null && distance > 0 && duration != null) {
            return duration / distance;
        }
        return null;
    }

}
