package com.example.RunAgainstTheWind.domain.achievement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "achievement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    @Id
    private String achievementName;
    private String description;
}
