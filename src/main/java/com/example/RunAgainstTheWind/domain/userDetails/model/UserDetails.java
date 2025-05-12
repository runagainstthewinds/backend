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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_details")
@Data
@EqualsAndHashCode(exclude = "user")
@NoArgsConstructor
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDetailsId;

    private Double totalDistance;
    private Double totalDuration;
    private Double weeklyDistance;
    private Integer runCount;

    @OneToOne(mappedBy = "userDetails")
    private User user;

    public UserDetails(Double totalDistance, Double totalDuration, Double weeklyDistance, Integer runCount, User user) {
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.weeklyDistance = weeklyDistance;
        this.runCount = runCount;
        this.user = user;
    }
}
