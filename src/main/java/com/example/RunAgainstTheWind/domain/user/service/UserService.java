package com.example.RunAgainstTheWind.domain.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.application.auth.JWTService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;

/*
 * Service responsible for handling user registration and login
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public User register(User user){ 
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Transactional
    public String verify(User user){
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername());
        
        return "Fail";
    }
}
