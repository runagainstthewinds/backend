package com.example.RunAgainstTheWind.dto.trainingPlan;

import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.enumeration.Road;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class TrainingPlanDTO {
    private Long trainingPlanId;
    private UUID userId;
    private Date startDate;
    private Date endDate;
    private String planType;
    private Road roadType;
    private Double goalDistance;
    private Double goalTime;
    private List<TrainingSessionDTO> trainingSessions = new ArrayList<>();
}