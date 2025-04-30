package com.example.RunAgainstTheWind.domain.userDetails.model;

import com.example.RunAgainstTheWind.domain.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_details")
@Data
@EqualsAndHashCode(exclude = "user")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDetailsId;

    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;

    public UserDetails() {}

    public UserDetails(Double totalDistance, Double totalDuration, Double weeklyDistance, Integer runCount) {
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.weeklyDistance = weeklyDistance;
        this.runCount = runCount;
    }

    @OneToOne(mappedBy = "userDetails")
    private User user;
}
