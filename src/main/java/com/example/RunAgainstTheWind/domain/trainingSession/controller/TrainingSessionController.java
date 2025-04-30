package com.example.RunAgainstTheWind.domain.trainingSession.controller;

import com.example.RunAgainstTheWind.domain.trainingSession.service.TrainingSessionService;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainingsessions")
public class TrainingSessionController {

    @Autowired
    private TrainingSessionService trainingSessionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<TrainingSessionDTO>> getTrainingSessionsByUserId(@PathVariable UUID userId) {
        List<TrainingSessionDTO> sessions = trainingSessionService.getTrainingSessionsByUserId(userId);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<TrainingSessionDTO> createTrainingSession(
            @PathVariable UUID userId,
            @RequestBody TrainingSessionDTO trainingSessionDTO) {
        TrainingSessionDTO createdSession = trainingSessionService.createTrainingSession(userId, trainingSessionDTO);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    @DeleteMapping("/{trainingSessionId}")
    public ResponseEntity<Void> deleteTrainingSession(@PathVariable Long trainingSessionId) {
        trainingSessionService.deleteTrainingSession(trainingSessionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{trainingSessionId}")
    public ResponseEntity<TrainingSessionDTO> updateTrainingSession(
            @PathVariable Long trainingSessionId,
            @RequestBody TrainingSessionDTO trainingSessionDTO) {
        TrainingSessionDTO updatedSession = trainingSessionService.updateTrainingSession(trainingSessionId, trainingSessionDTO);
        return new ResponseEntity<>(updatedSession, HttpStatus.OK);
    }
}
