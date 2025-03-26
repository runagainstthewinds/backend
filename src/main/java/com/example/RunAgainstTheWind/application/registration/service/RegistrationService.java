package com.example.RunAgainstTheWind.application.registration.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.registration.model.RegistrationRequest;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.repository.AppUserRepository;
import com.example.RunAgainstTheWind.enumeration.AppUserRole;

import lombok.AllArgsConstructor;

/*
 * Handle the registration of a new user
 */
@Service
@AllArgsConstructor
public class RegistrationService {
    private AppUserRepository appUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Check if the email is valid and if it is, sign up the user
    public String register(RegistrationRequest request) {
        boolean userExists = appUserRepository.findByEmail(request.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException("Email already taken");
        }

        return signUpUser(
            new AppUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
            )
        );
    }

    // Add the user to the database
    public String signUpUser(AppUser appUser) {
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        return "User has been registered";
    }
}