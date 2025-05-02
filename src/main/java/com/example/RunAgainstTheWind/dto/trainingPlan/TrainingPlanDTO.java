package com.example.RunAgainstTheWind.dto.trainingPlan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanDTO {
    private Long trainingPlanId;
    private String planName;
    private Date startDate;
    private Date endDate;
    private boolean isComplete;
    private Double goalDistance;
    private Double goalTime;
    private UUID userId;
}