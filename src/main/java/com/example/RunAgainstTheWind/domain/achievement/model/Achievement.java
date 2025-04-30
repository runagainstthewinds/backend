package com.example.RunAgainstTheWind.domain.achievement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name = "achievement")
@Data
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    private String name;
    private String description;

    public Achievement() {
    }

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
