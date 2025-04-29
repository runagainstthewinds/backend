package com.example.RunAgainstTheWind.dto.user;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID    userId;
    private String  username;
    private String  email;
    private String  googleCalendarToken;
    private String  stravaToken;
    private String  stravaRefreshToken;
}
