package com.example.RunAgainstTheWind.application.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.application.user.model.Users;
import com.example.RunAgainstTheWind.application.user.service.UserService;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping("/auth")    
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public Users register(@RequestBody Users user){
        try {
            return service.register(user);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }
}