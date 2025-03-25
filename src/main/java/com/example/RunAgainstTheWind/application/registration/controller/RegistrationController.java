package com.example.RunAgainstTheWind.application.registration.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.application.registration.model.RegistrationRequest;
import com.example.RunAgainstTheWind.application.registration.service.RegistrationService;

import lombok.AllArgsConstructor;

/* 
 * Controller responsible to handle request for new app user registration
 */
@RestController
@RequestMapping("/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }
}