package com.example.RunAgainstTheWind.domain.user.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.service.UserService;
import com.example.RunAgainstTheWind.dto.user.UserDTO;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping   
public class UserController {

    @Autowired
    private UserService userService;

    // no transactional, or else unexpected rollbackException
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
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
            return userService.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO dto = userService.findDTOByUsername(username);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}