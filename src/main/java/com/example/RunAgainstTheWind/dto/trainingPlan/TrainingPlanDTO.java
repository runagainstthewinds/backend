package com.example.RunAgainstTheWind.dto.trainingPlan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanDTO {
    private Long trainingPlanId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double goalDistance;
    private Double goalTime;
    private boolean isComplete;
    private UUID userId;
}