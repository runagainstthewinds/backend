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

    private Long trainingPlanId; 
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
    private Long shoeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;
}