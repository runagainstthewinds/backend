package com.example.RunAgainstTheWind.domain.trainingPlan.repository;

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
            tp.user.userId,
            tp.startDate,
            tp.endDate,
            tp.planType,
            tp.roadType,
            tp.goalDistance,
            tp.goalTime
        )
        FROM TrainingPlan tp
        WHERE tp.user.userId = :userUUID
    """)
    TrainingPlanDTO getTrainingPlanByUserId(@Param("userUUID") UUID userUUID);
}
