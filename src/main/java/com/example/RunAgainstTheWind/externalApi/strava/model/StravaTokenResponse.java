package com.example.RunAgainstTheWind.externalApi.strava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StravaTokenResponse {
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("expires_at")
    private Long expiresAt;
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("athlete")
    private StravaAthleteResponse athlete;
}