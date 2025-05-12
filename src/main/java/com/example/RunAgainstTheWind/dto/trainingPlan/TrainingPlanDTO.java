package com.example.RunAgainstTheWind.dto.trainingPlan;

import com.example.RunAgainstTheWind.enumeration.Difficulty;
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
public class TrainingPlanDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long trainingPlanId;

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double goalDistance;
    private Difficulty difficulty;
    private Boolean isComplete;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;
}