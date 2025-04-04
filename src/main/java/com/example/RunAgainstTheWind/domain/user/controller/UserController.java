package com.example.RunAgainstTheWind.domain.user.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.service.UserService;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping("/auth")    
public class UserController {

    @Autowired
    private UserService service;

    // no transactional, or else unexpected rollbackException
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = service.register(user);
            return ResponseEntity.ok(registeredUser);

        // catch if username is already in db
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Transactional
    @PostMapping("/login")
    public String login(@RequestBody User user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }
}