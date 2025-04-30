package com.example.RunAgainstTheWind.domain.userDetails.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

@RestController
@RequestMapping("/userdetails")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable("userId") UUID userId) {
        try {
            UserDetailsDTO userDetails = userDetailsService.getUserDetailsById(userId);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PutMapping("/{userId}")
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
