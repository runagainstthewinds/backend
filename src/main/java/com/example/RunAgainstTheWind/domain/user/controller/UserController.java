package com.example.RunAgainstTheWind.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/register")
    public User register(@RequestBody User user){
        try {
            return service.register(user);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }
}