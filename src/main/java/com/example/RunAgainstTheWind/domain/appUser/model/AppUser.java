package com.example.RunAgainstTheWind.domain.appUser.model;

import java.util.List;
import java.util.UUID;

import com.example.RunAgainstTheWind.domain.appUserDetails.model.AppUserDetails;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

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
@Table(name = "app_user")
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID appUserId; 

    private String username;
    private String email;
    private String password;

    @OneToOne
    @JoinColumn(name = "appUserDetailsID")
    private AppUserDetails appUserDetails;

    @OneToMany(mappedBy = "appUser")
    private List<TrainingSession> trainingSessions;

    @OneToOne
    @JoinColumn(name = "trainingPlanId")
    private TrainingPlan trainingPlan;
}
