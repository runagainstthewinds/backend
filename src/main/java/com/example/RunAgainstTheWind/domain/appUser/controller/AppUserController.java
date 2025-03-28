package com.example.RunAgainstTheWind.domain.appUser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.service.AppUserService;

/*
 * Controller responsible for handling authentication for a user trying to access the api.
 */
@RestController
@RequestMapping("/auth")    
public class AppUserController {

    @Autowired
    private AppUserService service;

    @PostMapping("/register")
    public AppUser register(@RequestBody AppUser user){
        try {
            return service.register(user);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody AppUser user){
        try {
            return service.verify(user);
        } catch (Exception e) {
            return "Fail"; 
        }
    }
}