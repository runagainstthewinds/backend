package com.example.RunAgainstTheWind.domain.appUserDetails.model;

import java.util.List;

import com.example.RunAgainstTheWind.domain.achievement.model.Achievement;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_user_details")
@Getter
@Setter
public class AppUserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appUserDetailsId;

    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;

    public AppUserDetails() {}

    public AppUserDetails(Double totalDistance, Double totalDuration, Double weeklyDistance, Integer runCount) {
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.weeklyDistance = weeklyDistance;
        this.runCount = runCount;
    }

    @OneToMany(mappedBy = "appUserDetails")
    private List<Achievement> achievements;

    @OneToOne(mappedBy = "appUserDetails")
    private AppUser appUser;
}
