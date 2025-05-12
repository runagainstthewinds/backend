package com.example.RunAgainstTheWind.dto.trainingSession;

import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingSessionDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    private Long trainingPlanId; 
    private Long shoeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    public TrainingSessionDTO(TrainingType trainingType, LocalDate date, Double distance, Double duration, Double pace, Boolean isComplete, Double achievedDistance, Double achievedDuration, Double achievedPace, Integer effort, String notes, Long trainingPlanId, Long shoeId, UUID userId) {
        this.trainingType = trainingType;
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.pace = pace;
        this.isComplete = isComplete;
        this.achievedDistance = achievedDistance;
        this.achievedDuration = achievedDuration;
        this.achievedPace = achievedPace;
        this.effort = effort;
        this.notes = notes;
        this.trainingPlanId = trainingPlanId;
        this.shoeId = shoeId;
        this.userId = userId;
    }
}