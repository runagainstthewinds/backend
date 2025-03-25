package com.example.RunAgainstTheWind.domain.trainingSession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    
}
