package com.example.RunAgainstTheWind.domain.user.model;

import java.util.UUID;

import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Table(name = "`user`")
@EqualsAndHashCode(exclude = {"userDetails"})
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    
    private String username;
    private String email;
    private String password;
    private String googleCalendarToken;
    
    private String stravaToken;
    private String stravaRefreshToken;
    private Long stravaTokenExpiresAt;
    private Long stravaAthleteId;
    
    @OneToOne
    @JoinColumn(name = "userDetailsID")
    private UserDetails userDetails;
}
