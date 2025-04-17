package com.example.RunAgainstTheWind.domain.achievement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    boolean existsById(Long achievementId);
}
