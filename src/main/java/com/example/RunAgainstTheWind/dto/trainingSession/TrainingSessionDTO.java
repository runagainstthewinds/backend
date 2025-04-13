package com.example.RunAgainstTheWind.dto.trainingSession;

import com.example.RunAgainstTheWind.dto.user.UserDTO;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import lombok.Data;

import java.util.Date;

@Data
public class TrainingSessionDTO {
    private Long trainingSessionId;
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
    private UserDTO user; // Reference to UserDTO
}