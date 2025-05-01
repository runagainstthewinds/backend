package com.example.RunAgainstTheWind.domain.achievement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.achievement.model.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    Optional<UserAchievement> findByUser_UserIdAndAchievement_AchievementName(UUID userId, String achievementName);

    //Find all achievements for a user
    @Query(value = "SELECT ua.achievement_name, a.description, ua.date_achieved " +
               "FROM user_achievement ua " +
               "JOIN achievement a ON a.achievement_name = ua.achievement_name " +
               "WHERE ua.user_id = :userId",
       nativeQuery = true)
    List<Object[]> findAchievementsByUserId(UUID userId);
    
    //  Check for duplicate assignment
    @Query(value = "SELECT count(*) FROM user_achievement ua WHERE ua.user_id = ?1 AND ua.achievement_name = ?2", nativeQuery = true)
    int existsUserAchievement(UUID userId, String achievementName);

    //  Assign achievement to user
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO user_achievement (user_id, achievement_name, date_achieved)
        VALUES (UNHEX(REPLACE(?1, '-', '')), ?2, ?3)
        """, nativeQuery = true)
    void assignAchievementToUser(String userId, String achievementName, LocalDate dateAchieved);
}
