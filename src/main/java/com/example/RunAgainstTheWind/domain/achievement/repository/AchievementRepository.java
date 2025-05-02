package com.example.RunAgainstTheWind.domain.achievement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.dto.achievement.AchievementDTO;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    boolean existsByAchievementId(Integer achievementId);

    // Find all the achievements of a user by their id
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.achievement.AchievementDTO(
            a.achievementId,
            a.achievementName,
            a.description,
            ua.dateAchieved,
            u.userId
        )
        FROM UserAchievement ua
        JOIN ua.achievement a
        JOIN ua.user u
        WHERE u.userId = :userId
    """)
    List<AchievementDTO> findAchievementsByUserId(@Param("userId") UUID userId);
}
