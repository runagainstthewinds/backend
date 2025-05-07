package com.example.RunAgainstTheWind.domain.trainingPlan.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.trainingPlan.service.TrainingPlanService;
import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;

@RestController
@RequestMapping("/trainingplans")
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<TrainingPlanDTO>> getTrainingPlanByUserId(@PathVariable UUID userId) {
        List<TrainingPlanDTO> trainingPlan = trainingPlanService.getTrainingPlanByUserId(userId);
        return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
    }

    @GetMapping("/current/{userId}")
    public ResponseEntity<TrainingPlanDTO> getCurrentTrainingPlanByUserId(@PathVariable UUID userId) {
        TrainingPlanDTO trainingPlan = trainingPlanService.getCurrentTrainingPlanByUserId(userId);
        if (trainingPlan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<TrainingPlanDTO> createTrainingPlan(
            @PathVariable UUID userId,
            @RequestBody TrainingPlanDTO trainingPlanDTO) {
        TrainingPlanDTO savedTrainingPlan = trainingPlanService.createTrainingPlan(userId, trainingPlanDTO);
        return new ResponseEntity<>(savedTrainingPlan, HttpStatus.CREATED);
    }

    @DeleteMapping("/{trainingPlanId}")
    public ResponseEntity<?> deleteTrainingPlan(@PathVariable Long trainingPlanId) {
        trainingPlanService.deleteTrainingPlan(trainingPlanId);
        return ResponseEntity.ok(Map.of("message", "Training Plan with ID " + trainingPlanId + " has been deleted"));
    }
}
