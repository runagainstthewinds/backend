package com.example.RunAgainstTheWind.domain.achievement.model;

import com.example.RunAgainstTheWind.domain.appUserDetails.model.AppUserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "achievement")
@Getter
@Setter
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    private String name;
    private String description;
    private boolean isCompleted;

    public Achievement() {
    }

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.isCompleted = false;
    }

    @ManyToOne
    @JoinColumn(name = "appUserDetailsID")
    private AppUserDetails appUserDetails;
}
