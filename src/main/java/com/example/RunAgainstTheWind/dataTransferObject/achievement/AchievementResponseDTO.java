package com.example.RunAgainstTheWind.dataTransferObject.achievement;

import java.sql.Date;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;

import lombok.Data;

@Data
public class AchievementResponseDTO {
    
    private Long achievementId;
    private String name;
    private String description;
    private boolean isCompleted;
    private Date createdAt;

    public AchievementResponseDTO() {}

    public AchievementResponseDTO(Achievement achievement) {
        this.achievementId = achievement.getAchievementId();
        this.name = achievement.getName();
        this.description = achievement.getDescription();
        this.isCompleted = achievement.isCompleted();
        this.createdAt = new java.sql.Date(System.currentTimeMillis());;
    }
}
