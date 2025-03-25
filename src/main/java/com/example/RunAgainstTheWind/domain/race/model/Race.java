package com.example.RunAgainstTheWind.domain.race.model;

import java.util.Date;

import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "race")
@Getter
@Setter
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long raceId;

    private Date date;
    private String location;
    private Double distance;

    public Race() {}

    public Race(Date date, String location, Double distance) {
        this.date = date;
        this.location = location;
        this.distance = distance;
    }

    @OneToOne(mappedBy = "race")
    private TrainingPlan trainingPlan;
}
