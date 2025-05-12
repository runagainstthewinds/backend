package com.example.RunAgainstTheWind.domain.trainingPlan.model;

import java.time.LocalDate;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.enumeration.Difficulty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "training_plan")
@Data
@NoArgsConstructor
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingPlanId;

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double goalDistance;
    private Difficulty difficulty;
    private boolean isComplete;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public TrainingPlan(String planName, LocalDate startDate, LocalDate endDate, Double goalDistance, Difficulty difficulty, boolean isComplete, User user) {
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalDistance = goalDistance;
        this.difficulty = difficulty;
        this.isComplete = isComplete;
        this.user = user;
    }
}   
