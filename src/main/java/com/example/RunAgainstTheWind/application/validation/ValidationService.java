package com.example.RunAgainstTheWind.application.validation;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.exceptions.UserNotFoundException;
import com.example.RunAgainstTheWind.exceptions.InvalidTrainingSessionException;

@Service
public class ValidationService {
    @Autowired
    private UserRepository userRepository;

    public void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public User validateUserExistsAndReturn(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public void validateStringInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
    }

    public void validateIntegerInput(Integer input) {
        if (input == null || input <= 0) {
            throw new IllegalArgumentException("Input must be a positive integer");
        }
    }

    public void validateTrainingSession(TrainingSessionDTO trainingSession) {
        if (trainingSession == null) {
            throw new InvalidTrainingSessionException("Training session cannot be null");
        }
        if (!Boolean.TRUE.equals(trainingSession.getIsComplete())) {
            throw new InvalidTrainingSessionException("Training session must be completed to evaluate achievements");
        }
        if (trainingSession.getAchievedDistance() == null) {
            throw new InvalidTrainingSessionException("Achieved distance cannot be null");
        }
        if (trainingSession.getAchievedDuration() == null) {
            throw new InvalidTrainingSessionException("Achieved duration cannot be null");
        }
        if (trainingSession.getAchievedPace() == null) {
            throw new InvalidTrainingSessionException("Achieved pace cannot be null");
        }
    }
}
