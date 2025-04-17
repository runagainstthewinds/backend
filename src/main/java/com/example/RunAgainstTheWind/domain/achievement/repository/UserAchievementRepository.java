package com.example.RunAgainstTheWind.domain.achievement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<Achievement, Long> {
    @Query("SELECT a FROM User u JOIN u.achievements a WHERE u.userId = :userId")
    List<Achievement> findAchievementsByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_achievement (user_id, achievement_id) VALUES (:userId, :achievementId)", nativeQuery = true)
    void assignAchievementToUser(UUID userId, Long achievementId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM user_achievement WHERE user_id = :userId AND achievement_id = :achievementId)", nativeQuery = true)
    boolean existsByUserIdAndAchievementId(UUID userId, Long achievementId);
}
