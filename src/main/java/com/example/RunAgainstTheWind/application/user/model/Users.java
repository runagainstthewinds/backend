package com.example.RunAgainstTheWind.application.user.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * Model of a User who is trying to access the API.
 */
@Data
@Table(name = "app_user")
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Users {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID appUserId; 

    private String username;
    private String email;
    private String password;
}
