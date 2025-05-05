package com.example.RunAgainstTheWind.domain.user.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.application.auth.JWTService;
import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.user.UserDTO;
import com.example.RunAgainstTheWind.dto.user.UserUpdateDTO;

/*
 * Service responsible for handling user registration and login
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private ValidationService v;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public User register(User user) throws IllegalArgumentException {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public String verify(User user){
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername());
        
        return "Fail";
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Transactional
    public UserDTO updateUserSettings(UUID userId, UserUpdateDTO userDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null) user.setPassword(encoder.encode(userDTO.getPassword()));

        UserDTO dto = new UserDTO(
            user.getUserId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getGoogleCalendarToken(), 
            user.getStravaToken(), 
            user.getStravaRefreshToken());
        return dto;
    }
}
