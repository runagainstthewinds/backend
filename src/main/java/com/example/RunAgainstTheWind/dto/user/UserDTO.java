package com.example.RunAgainstTheWind.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID userId;
    private String username;
    private String email;
}
