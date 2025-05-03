package com.example.RunAgainstTheWind.dto.achievement;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AchievementDTO {
    private Integer achievementId;
    private String achievementName;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate dateAchieved;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;
} 
