package com.example.RunAgainstTheWind.dto.trainingSession;

import com.example.RunAgainstTheWind.enumeration.TrainingType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TrainingSessionDTO {
    private Long trainingSessionId;
    private UUID userId;
    private Date date;
    private Double distance;
    private Double duration;
    private Double goalPace;
    private Boolean isCompleted;
    private Double achievedPace;
    private Double achievedDistance;
    private Double achievedDuration;
    private Integer effort;
    private TrainingType trainingType;
}