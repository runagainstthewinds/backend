package com.example.RunAgainstTheWind.application.validation;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.exceptions.UserNotFoundException;

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
}
