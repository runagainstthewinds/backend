package com.example.RunAgainstTheWind.domain.trainingPlan.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long>{
    // Find the user details by user ID
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO(
            tp.trainingPlanId,
            tp.planName,
            tp.startDate,
            tp.endDate,
            tp.goalDistance,
            tp.difficulty,
            tp.isComplete,
            tp.user.userId
        )
        FROM TrainingPlan tp
        WHERE tp.user.userId = :userUUID
    """)
    List<TrainingPlanDTO> getTrainingPlanByUserId(@Param("userUUID") UUID userUUID);


    // Find the current training plan by user ID
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.trainingPlan.TrainingPlanDTO(
            tp.trainingPlanId,
            tp.planName,
            tp.startDate,
            tp.endDate,
            tp.goalDistance,
            tp.difficulty,
            tp.isComplete,
            tp.user.userId
        )
        FROM TrainingPlan tp
        WHERE tp.user.userId = :userUUID AND tp.isComplete = false
    """)
    Optional<TrainingPlanDTO> getCurrentTrainingPlanByUserId(@Param("userUUID") UUID userUUID);
}
