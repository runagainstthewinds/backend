package com.example.RunAgainstTheWind.domain.userDetails.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.domain.userDetails.repository.UserDetailsRepository;
import com.example.RunAgainstTheWind.dto.UserDetailsDTO;

@Service
public class UserDetailsService {
    
    private final UserDetailsRepository userDetailsRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserDetailsRepository userDetailsRepository, UserRepository userRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.userRepository = userRepository;
    }

    public UserDetailsDTO getUserDetailsById(Long userId) {
        Long userDetailsId = userRepository.findById(userId.intValue()).map(user -> user.getUserDetails().getUserDetailsId()).orElse(null);
        UserDetails userDetails = userDetailsRepository.findById(userId).orElse(null);

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserDetailsId(userDetailsId);
    }


}