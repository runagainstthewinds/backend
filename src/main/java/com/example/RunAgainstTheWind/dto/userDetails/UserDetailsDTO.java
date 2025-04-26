package com.example.RunAgainstTheWind.dto.userDetails;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long userDetailsId;
    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;
}