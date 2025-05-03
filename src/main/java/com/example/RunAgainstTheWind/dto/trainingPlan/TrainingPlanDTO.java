package com.example.RunAgainstTheWind.dto.trainingPlan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingPlanDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long trainingPlanId;

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double goalDistance;
    private Double goalTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isComplete;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)   
    private UUID userId;
}