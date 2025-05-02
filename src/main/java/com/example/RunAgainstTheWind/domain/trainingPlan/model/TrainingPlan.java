package com.example.RunAgainstTheWind.domain.trainingPlan.model;

import java.util.Date;

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
    private Date startDate;
    private Date endDate;
    private boolean isComplete;
    private Double goalDistance;
    private Double goalTime;

    public TrainingPlan() {}

    public TrainingPlan(String planName, Date startDate, Date endDate, Boolean isComplete, Double goalDistance, Double goalTime) {
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isComplete = isComplete;
        this.goalDistance = goalDistance;
        this.goalTime = goalTime;
    }

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}   
