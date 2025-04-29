package com.example.RunAgainstTheWind.domain.user.controller;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.trainingPlan.service.TrainingPlanService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.service.UserService;
import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.dto.user.UserDTO;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping   
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TrainingPlanService trainingPlanService;

    // no transactional, or else unexpected rollbackException
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = service.register(user);
            return ResponseEntity.ok(registeredUser);

        // catch if username is already in db
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Transactional
    @PostMapping("/auth/login")
    public String login(@RequestBody User user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }

    @Transactional
    @GetMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable("userId") UUID userId) {
        try {
            UserDetailsDTO userDetails = userDetailsService.getUserDetailsById(userId);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> createUserDetails(
            @PathVariable("userId") UUID userId,
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            UserDetailsDTO createdDetails = userDetailsService.createUserDetails(userId, userDetailsDTO);
            return new ResponseEntity<>(createdDetails, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PutMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(
            @PathVariable("userId") UUID userId,
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            UserDetailsDTO updatedDetails = userDetailsService.updateUserDetails(userId, userDetailsDTO);
            return new ResponseEntity<>(updatedDetails, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{userId}/trainingplan")
    public ResponseEntity<TrainingPlanDTO> getTrainingPlanByUserId(@PathVariable UUID userId) {
        TrainingPlanDTO trainingPlan = trainingPlanService.getTrainingPlanByUserId(userId);
        if (trainingPlan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/trainingplan")
    public ResponseEntity<TrainingPlanDTO> createTrainingPlan(
            @PathVariable UUID userId,
            @RequestBody TrainingPlanDTO trainingPlanDTO) {
        TrainingPlanDTO savedTrainingPlan = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);
        return new ResponseEntity<>(savedTrainingPlan, HttpStatus.CREATED);
    }

    @PutMapping("/users/{userId}/trainingplan")
    public ResponseEntity<TrainingPlanDTO> updateTrainingPlan(
            @PathVariable UUID userId,
            @RequestBody TrainingPlanDTO trainingPlanDTO) {
        TrainingPlanDTO updatedTrainingPlan = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);
        return new ResponseEntity<>(updatedTrainingPlan, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/trainingplan")
    public ResponseEntity<Void> deleteTrainingPlan(@PathVariable UUID userId) {
        trainingPlanService.deleteTrainingPlan(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO dto = service.findDtoByUsername(username);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}