package com.example.RunAgainstTheWind.externalApi.strava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;

@Data
public class StravaAthleteResponse {
    
    private Long id;
    private String username;
    
    @JsonProperty("resource_state")
    private Integer resourceState;
    
    private String firstname;
    private String lastname;
    private String bio;
    private String city;
    private String state;
    private String country;
    private String sex;
    private Boolean premium;
    private Boolean summit;
    
    @JsonProperty("created_at")
    private Instant createdAt;
    
    @JsonProperty("updated_at")
    private Instant updatedAt;
    
    @JsonProperty("badge_type_id")
    private Integer badgeTypeId;
    
    private Float weight;
    
    @JsonProperty("profile_medium")
    private String profileMedium;
    
    private String profile;
    private String friend;
    private String follower;
}
