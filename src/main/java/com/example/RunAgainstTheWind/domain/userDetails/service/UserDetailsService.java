package com.example.RunAgainstTheWind.domain.userDetails.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.domain.userDetails.repository.UserDetailsRepository;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

@Service
public class UserDetailsService {
    
    private final UserDetailsRepository userDetailsRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserDetailsRepository userDetailsRepository, UserRepository userRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDetailsDTO getUserDetailsById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        UserDetails userDetails = user.getUserDetails();

        if (userDetails == null) {
            throw new RuntimeException("User details not found for userId: " + userId);
        }

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserDetailsId(userDetails.getUserDetailsId());
        dto.setTotalDistance(userDetails.getTotalDistance());
        dto.setTotalDuration(userDetails.getTotalDuration());
        dto.setWeeklyDistance(userDetails.getWeeklyDistance());
        dto.setRunCount(userDetails.getRunCount());
        return dto;
    }

    @Transactional
    public UserDetailsDTO createUserDetails(UUID userId, UserDetailsDTO userDetailsDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserDetails userDetails = new UserDetails();
        userDetails.setTotalDistance(userDetailsDTO.getTotalDistance() != null ? userDetailsDTO.getTotalDistance() : 0.0);
        userDetails.setTotalDuration(userDetailsDTO.getTotalDuration() != null ? userDetailsDTO.getTotalDuration() : 0.0);
        userDetails.setWeeklyDistance(userDetailsDTO.getWeeklyDistance() != null ? userDetailsDTO.getWeeklyDistance() : 0.0);
        userDetails.setRunCount(userDetailsDTO.getRunCount() != null ? userDetailsDTO.getRunCount() : 0);
        userDetails.setUser(user);

        UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
        user.setUserDetails(savedUserDetails);
        userRepository.save(user);

        UserDetailsDTO responseDto = new UserDetailsDTO();
        responseDto.setUserDetailsId(savedUserDetails.getUserDetailsId());
        responseDto.setTotalDistance(savedUserDetails.getTotalDistance());
        responseDto.setTotalDuration(savedUserDetails.getTotalDuration());
        responseDto.setWeeklyDistance(savedUserDetails.getWeeklyDistance());
        responseDto.setRunCount(savedUserDetails.getRunCount());

        return responseDto;
    }

    @Transactional
    public UserDetailsDTO updateUserDetails(UUID userId, UserDetailsDTO userDetailsDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) {
            throw new RuntimeException("User details not found for userId: " + userId);
        }

        if (userDetailsDTO.getTotalDistance() != null) {
            userDetails.setTotalDistance(userDetailsDTO.getTotalDistance());
        }
        if (userDetailsDTO.getTotalDuration() != null) {
            userDetails.setTotalDuration(userDetailsDTO.getTotalDuration());
        }
        if (userDetailsDTO.getWeeklyDistance() != null) {
            userDetails.setWeeklyDistance(userDetailsDTO.getWeeklyDistance());
        }
        if (userDetailsDTO.getRunCount() != null) {
            userDetails.setRunCount(userDetailsDTO.getRunCount());
        }

        UserDetails updatedUserDetails = userDetailsRepository.save(userDetails);

        UserDetailsDTO responseDto = new UserDetailsDTO();
        responseDto.setUserDetailsId(updatedUserDetails.getUserDetailsId());
        responseDto.setTotalDistance(updatedUserDetails.getTotalDistance());
        responseDto.setTotalDuration(updatedUserDetails.getTotalDuration());
        responseDto.setWeeklyDistance(updatedUserDetails.getWeeklyDistance());
        responseDto.setRunCount(updatedUserDetails.getRunCount());

        return responseDto;
    }
}