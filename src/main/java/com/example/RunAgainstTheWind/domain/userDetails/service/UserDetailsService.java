package com.example.RunAgainstTheWind.domain.userDetails.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.domain.userDetails.repository.UserDetailsRepository;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

@Service
public class UserDetailsService {
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private ValidationService v;

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserDetailsById(UUID userId) {
        return userDetailsRepository.getUserDetailsByUserId(userId);
    }

    @Transactional
    public UserDetailsDTO createUserDetails(UUID userId, UserDetailsDTO userDetailsDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        UserDetails userDetails = new UserDetails(
            userDetailsDTO.getTotalDistance() != null ? userDetailsDTO.getTotalDistance() : 0.0,
            userDetailsDTO.getTotalDuration() != null ? userDetailsDTO.getTotalDuration() : 0.0,
            userDetailsDTO.getWeeklyDistance() != null ? userDetailsDTO.getWeeklyDistance() : 0.0,
            userDetailsDTO.getRunCount() != null ? userDetailsDTO.getRunCount() : 0,
            user
        );
        UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
        user.setUserDetails(savedUserDetails);

        userDetailsDTO.setUserDetailsId(savedUserDetails.getUserDetailsId());
        userDetailsDTO.setUserId(user.getUserId());

        return userDetailsDTO;
    }

    @Transactional
    public UserDetailsDTO updateUserDetails(UUID userId, UserDetailsDTO userDetailsDTO) {
        User user = v.validateUserExistsAndReturn(userId);
        
        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) throw new RuntimeException("User details not found for userId: " + userId);
        if (userDetailsDTO.getTotalDistance() != null) userDetails.setTotalDistance(userDetailsDTO.getTotalDistance());
        if (userDetailsDTO.getTotalDuration() != null) userDetails.setTotalDuration(userDetailsDTO.getTotalDuration());
        if (userDetailsDTO.getWeeklyDistance() != null) userDetails.setWeeklyDistance(userDetailsDTO.getWeeklyDistance());
        if (userDetailsDTO.getRunCount() != null) userDetails.setRunCount(userDetailsDTO.getRunCount());
        
        userDetailsDTO.setUserDetailsId(userDetails.getUserDetailsId());
        userDetailsDTO.setUserId(user.getUserId());
        return userDetailsDTO;
    }
}
