package com.example.RunAgainstTheWind.domain.user.controller;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.service.UserService;
import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping   
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserDetailsService userDetailsService;

    // no transactional, or else unexpected rollbackException
    @PostMapping("/auth/register")
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
    @PostMapping("/auth/login")
    public String login(@RequestBody User user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }

    @Transactional
    @GetMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable("userId") UUID userId) {
        try {
            UserDetailsDTO userDetails = userDetailsService.getUserDetailsById(userId);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> createUserDetails(
            @PathVariable("userId") UUID userId,
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            UserDetailsDTO createdDetails = userDetailsService.createUserDetails(userId, userDetailsDTO);
            return new ResponseEntity<>(createdDetails, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PutMapping("/users/{userId}/userDetails")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(
            @PathVariable("userId") UUID userId,
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            UserDetailsDTO updatedDetails = userDetailsService.updateUserDetails(userId, userDetailsDTO);
            return new ResponseEntity<>(updatedDetails, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}