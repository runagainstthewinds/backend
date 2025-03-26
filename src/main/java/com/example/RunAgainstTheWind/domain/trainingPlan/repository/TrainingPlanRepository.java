package com.example.RunAgainstTheWind.domain.trainingPlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long>{
    
}
