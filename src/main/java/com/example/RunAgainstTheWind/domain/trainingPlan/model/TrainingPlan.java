package com.example.RunAgainstTheWind.domain.trainingPlan.model;

import java.util.Date;
import java.util.List;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.race.model.Race;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.Road;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "training_plan")
@Data
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingPlanId;

    private Date startDate;
    private Date endDate;
    private String planType;
    private Road roadType;
    private Double goalDistance;
    private Double goalTime;

    public TrainingPlan() {}

    public TrainingPlan(Date startDate, Date endDate, String planType, Road roadType, Double goalDistance, Double goalTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.planType = planType;
        this.roadType = roadType;
        this.goalDistance = goalDistance;
        this.goalTime = goalTime;
    }

    @OneToOne
    @JoinColumn(name = "raceId")
    private Race race;

    @OneToOne(mappedBy = "trainingPlan")
    private AppUser appUser;

    @OneToMany(mappedBy = "trainingPlan")
    private List<TrainingSession> trainingSessions;
}   
