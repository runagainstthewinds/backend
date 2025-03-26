package com.example.RunAgainstTheWind.application.registration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/*
 * Model class to represent the registration request
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;  
    private final String password;
    private final String email;
}
