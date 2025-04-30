package com.example.RunAgainstTheWind.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID    userId;
    private String  username;
    private String  email;
    private String  googleCalendarToken;
    private String  stravaToken;
    private String  stravaRefreshToken;
}