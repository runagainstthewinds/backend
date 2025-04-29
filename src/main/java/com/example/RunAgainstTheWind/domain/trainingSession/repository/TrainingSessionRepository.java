package com.example.RunAgainstTheWind.domain.trainingSession.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.user.model.User;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByUser(User user);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user = :user " +
           "AND ts.date = :date " +
           "AND ABS(ts.achievedDistance - :achievedDistance) < 0.01 " +
           "AND ABS(ts.achievedDuration - :achievedDuration) < 0.1")
    Optional<TrainingSession> findDuplicateSession(
            @Param("user") User user,
            @Param("date") Date date,
            @Param("achievedDistance") Double achievedDistance,
            @Param("achievedDuration") Double achievedDuration
    );
}
