package com.example.RunAgainstTheWind.domain.trainingSession.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByUser(User user);
    
    // Find duplicate training sessions
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user = :user " +
           "AND ts.date = :date " +
           "AND ABS(ts.achievedDistance - :achievedDistance) < 0.01 " +
           "AND ABS(ts.achievedDuration - :achievedDuration) < 0.1")
    Optional<TrainingSession> findDuplicateSession(
            @Param("user") User user,
            @Param("date") LocalDate date,
            @Param("achievedDistance") Double achievedDistance,
            @Param("achievedDuration") Double achievedDuration
    );
    
    // Find training sessions by user ID
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO(
            ts.trainingSessionId,
            ts.trainingType,
            ts.date,
            ts.distance,
            ts.duration,
            ts.pace,
            ts.isComplete,
            ts.achievedDistance,
            ts.achievedDuration,
            ts.achievedPace,
            ts.effort,
            ts.notes,
            ts.trainingPlan.trainingPlanId,
            ts.shoe.shoeId,
            ts.user.userId
        )
        FROM TrainingSession ts
        WHERE ts.user.userId = :userUUID
    """)
    List<TrainingSessionDTO> getTrainingSessionsByUserId(@Param("userUUID") UUID userUUID);

    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO(
            ts.trainingSessionId,
            ts.trainingType,
            ts.date,
            ts.distance,
            ts.duration,
            ts.pace,
            ts.isComplete,
            ts.achievedDistance,
            ts.achievedDuration,
            ts.achievedPace,
            ts.effort,
            ts.notes,
            ts.trainingPlan.trainingPlanId,
            ts.shoe.shoeId,
            ts.user.userId
        )
        FROM TrainingSession ts
        WHERE ts.trainingPlan.trainingPlanId = :trainingPlanId
    """)
    List<TrainingSessionDTO> findByTrainingPlanId(@Param("trainingPlanId") Long trainingPlanId);
}
