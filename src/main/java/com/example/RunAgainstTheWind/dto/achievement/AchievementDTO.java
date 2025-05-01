package com.example.RunAgainstTheWind.dto.achievement;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDTO {
    private String achievementName;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAchieved;
    private UUID userId;
}
