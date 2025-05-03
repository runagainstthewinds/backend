package com.example.RunAgainstTheWind.dto.userDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userDetailsId;

    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;
}
