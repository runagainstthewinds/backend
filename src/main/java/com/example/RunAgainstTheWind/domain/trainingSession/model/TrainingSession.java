package com.example.RunAgainstTheWind.domain.trainingSession.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

@Entity
@Table(name = "training_session")
@Data
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingSessionId;

    private TrainingType trainingType; 
    private LocalDate date;
    private Double distance;
    private Double duration;
    private Double pace;
    private Boolean isComplete;
    private Double achievedDistance;
    private Double achievedDuration;
    private Double achievedPace;
    private Integer effort;
    private String notes; 

    public TrainingSession() {}

    public TrainingSession(TrainingType trainingType, LocalDate date, Double distance, Double duration, Double pace, Boolean isComplete, Double achievedDistance, Double achievedDuration, Double achievedPace, Integer effort, String notes) {
        this.trainingType = trainingType; 
        this.date = date;
        this.distance = distance; 
        this.duration = duration; 
        this.pace = pace; 
        this.isComplete = isComplete;
        this.achievedPace = achievedPace;
        this.achievedDistance = achievedDistance;
        this.achievedDuration = achievedDuration;
        this.effort = effort;
        this.notes = notes;
    }

    @ManyToOne
    @JoinColumn(name = "trainingPlanId")
    private TrainingPlan trainingPlan;

    @ManyToOne
    @JoinColumn(name = "shoeId")
    private Shoe shoe;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
