package com.example.RunAgainstTheWind.domain.trainingSession.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.user.model.User;

@Entity
@Table(name = "training_session")
@Data
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingSessionId;

    private Date date;
    private Double distance;
    private Double duration;
    private Double goalPace;
    private Boolean isCompleted;
    private Double achievedPace;
    private Double achievedDistance;
    private Double achievedDuration;
    private Integer effort;

    public TrainingSession() {}

    public TrainingSession(Date date, Double distance, Double duration, Double goalPace, Boolean isCompleted, Double achievedPace, Double achievedDistance, Double achievedDuration, Integer effort) {
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.goalPace = goalPace;
        this.isCompleted = isCompleted;
        this.achievedPace = achievedPace;
        this.achievedDistance = achievedDistance;
        this.achievedDuration = achievedDuration;
        this.effort = effort;
    }

    @ManyToOne
    @JoinColumn(name = "shoeId")
    private Shoe shoe;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainingPlanId")
    private TrainingPlan trainingPlan;
}
