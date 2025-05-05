package com.example.RunAgainstTheWind.dto.user;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDTO {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    private String username;
    private String email;
    private String password;
}
