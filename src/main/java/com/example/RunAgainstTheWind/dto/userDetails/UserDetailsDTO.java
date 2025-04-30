package com.example.RunAgainstTheWind.dto.userDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private Long userDetailsId;
    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;
}
