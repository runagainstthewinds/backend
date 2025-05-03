package com.example.RunAgainstTheWind.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID    userId;
    
    private String  username;
    private String  email;
    private String  googleCalendarToken;
    private String  stravaToken;
    private String  stravaRefreshToken;
}