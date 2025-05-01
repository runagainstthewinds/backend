package com.example.RunAgainstTheWind.domain.user.model;

import java.util.List;
import java.util.UUID;

import com.example.RunAgainstTheWind.domain.achievement.model.UserAchievement;
import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * Model of a User who is trying to access the API.
 */
@Data
@Table(name = "`user`")
@EqualsAndHashCode(exclude = {"userDetails", "trainingPlan", "trainingSessions", "shoes"})
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    
    private String username;
    private String email;
    private String password;
    private String googleCalendarToken;
    
    private String stravaToken;
    private String stravaRefreshToken;
    private Long stravaTokenExpiresAt;
    private Long stravaAthleteId;
    
    @OneToOne
    @JoinColumn(name = "userDetailsID")
    private UserDetails userDetails;
    
    @OneToOne
    @JoinColumn(name = "trainingPlanId")
    private TrainingPlan trainingPlan;
    
    @OneToMany(mappedBy = "user")
    private List<TrainingSession> trainingSessions;
    
    @OneToMany(mappedBy = "user")
    private List<Shoe> shoes;

    @OneToMany(mappedBy = "user")
    private List<UserAchievement> userAchievements;
}
