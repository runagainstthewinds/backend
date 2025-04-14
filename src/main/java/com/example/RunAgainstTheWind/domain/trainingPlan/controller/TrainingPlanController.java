package com.example.RunAgainstTheWind.domain.trainingPlan.controller;

import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;
import com.example.RunAgainstTheWind.domain.trainingPlan.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trainingplans")
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @GetMapping("/{userId}")
    public ResponseEntity<TrainingPlanDTO> getTrainingPlanByUserId(@PathVariable UUID userId) {
        TrainingPlanDTO trainingPlan = trainingPlanService.getTrainingPlanByUserId(userId);
        return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<TrainingPlanDTO> createTrainingPlan(
            @PathVariable UUID userId,
            @RequestBody TrainingPlanDTO trainingPlanDTO) {
        TrainingPlanDTO savedTrainingPlan = trainingPlanService.createOrUpdateTrainingPlan(userId, trainingPlanDTO);
        return new ResponseEntity<>(savedTrainingPlan, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteTrainingPlan(@PathVariable UUID userId) {
        trainingPlanService.deleteTrainingPlan(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
