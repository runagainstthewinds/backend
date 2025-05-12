package com.example.RunAgainstTheWind.domain.achievement.model;

import java.time.LocalDate;

import com.example.RunAgainstTheWind.domain.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_achievement")
@Data
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id", referencedColumnName = "achievementId")
    private Achievement achievement;

    private LocalDate dateAchieved;

    public UserAchievement(User user, Achievement achievement, LocalDate dateAchieved) {
        this.user = user;
        this.achievement = achievement;
        this.dateAchieved = dateAchieved;
    }
}
