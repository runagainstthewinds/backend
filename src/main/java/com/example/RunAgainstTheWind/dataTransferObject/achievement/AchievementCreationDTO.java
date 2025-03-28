package com.example.RunAgainstTheWind.dataTransferObject.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AchievementCreationDTO {

    @NotBlank(message = "Achievement name cannot be blank")
    private String name;

    @NotNull(message = "Achievement description cannot be null")
    private String description;

    public AchievementCreationDTO() {}

    public AchievementCreationDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
