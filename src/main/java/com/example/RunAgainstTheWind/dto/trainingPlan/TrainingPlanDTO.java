package com.example.RunAgainstTheWind.dto.trainingPlan;

import com.example.RunAgainstTheWind.enumeration.Road;

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
    private UUID userId;
    private Date startDate;
    private Date endDate;
    private String planType;
    private Road roadType;
    private Double goalDistance;
    private Double goalTime;
}