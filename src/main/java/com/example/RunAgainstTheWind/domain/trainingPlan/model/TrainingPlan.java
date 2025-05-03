package com.example.RunAgainstTheWind.domain.trainingPlan.model;

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
@Table(name = "training_plan")
@Data
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingPlanId;

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double goalDistance;
    private Double goalTime;
    private boolean isComplete;

    public TrainingPlan() {}

    public TrainingPlan(String planName, LocalDate startDate, LocalDate endDate, Double goalDistance, Double goalTime, Boolean isComplete) {
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalDistance = goalDistance;
        this.goalTime = goalTime;
        this.isComplete = isComplete;
    }

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}   
