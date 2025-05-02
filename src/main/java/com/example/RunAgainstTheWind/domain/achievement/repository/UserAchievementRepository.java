package com.example.RunAgainstTheWind.domain.achievement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.achievement.model.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    //Find all achievements for a user
    List<UserAchievement> findByUser_UserId(UUID userId);
    
    //  Check for duplicate assignment
    boolean existsByUser_UserIdAndAchievement_AchievementId(UUID userId, Integer achievementId);
}
