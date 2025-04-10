package com.example.RunAgainstTheWind.serviceTesting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.domain.userDetails.repository.UserDetailsRepository;
import com.example.RunAgainstTheWind.domain.userDetails.service.UserDetailsService;
import com.example.RunAgainstTheWind.dto.UserDetailsDTO;

public class UserDetailsServiceTest {

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    private UUID userId;
    private User user;
    private UserDetails userDetails;
    private UserDetailsDTO userDetailsDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        userId = UUID.randomUUID();
        
        user = new User();
        user.setUserId(userId);
        user.setUsername("testuser");
        
        userDetails = new UserDetails();
        userDetails.setUserDetailsId(1L);
        userDetails.setTotalDistance(100.0);
        userDetails.setTotalDuration(200.0);
        userDetails.setWeeklyDistance(50.0);
        userDetails.setRunCount(5);
        userDetails.setUser(user);
        
        user.setUserDetails(userDetails);
        
        userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserDetailsId(userDetails.getUserDetailsId());
        userDetailsDTO.setTotalDistance(userDetails.getTotalDistance());
        userDetailsDTO.setTotalDuration(userDetails.getTotalDuration());
        userDetailsDTO.setWeeklyDistance(userDetails.getWeeklyDistance());
        userDetailsDTO.setRunCount(userDetails.getRunCount());
    }

    @Test
    public void testGetUserDetailsById_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // Act
        UserDetailsDTO result = userDetailsService.getUserDetailsById(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userDetails.getUserDetailsId(), result.getUserDetailsId());
        assertEquals(100.0, result.getTotalDistance());
        assertEquals(200.0, result.getTotalDuration());
        assertEquals(50.0, result.getWeeklyDistance());
        assertEquals(5, result.getRunCount());
        
        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetUserDetailsById_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.getUserDetailsById(userId);
        });
        
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetUserDetailsById_UserDetailsNotFound() {
        // Arrange
        User userWithoutDetails = new User();
        userWithoutDetails.setUserId(userId);
        userWithoutDetails.setUsername("testuser");
        userWithoutDetails.setUserDetails(null);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutDetails));
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.getUserDetailsById(userId);
        });
        
        assertEquals("User details not found for userId: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    public void testCreateUserDetails_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(invocation -> {
            UserDetails savedDetails = invocation.getArgument(0);
            savedDetails.setUserDetailsId(1L);
            return savedDetails;
        });
        
        // Create a new DTO for creating user details
        UserDetailsDTO inputDTO = new UserDetailsDTO();
        inputDTO.setTotalDistance(150.0);
        inputDTO.setTotalDuration(300.0);
        inputDTO.setWeeklyDistance(75.0);
        inputDTO.setRunCount(10);
        
        // Act
        UserDetailsDTO result = userDetailsService.createUserDetails(userId, inputDTO);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getUserDetailsId());
        assertEquals(150.0, result.getTotalDistance());
        assertEquals(300.0, result.getTotalDuration());
        assertEquals(75.0, result.getWeeklyDistance());
        assertEquals(10, result.getRunCount());
        
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).save(any(UserDetails.class));
        // Verify the user is updated with the new details
        verify(userRepository).save(user);
    }

    @Test
    public void testCreateUserDetails_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.createUserDetails(userId, new UserDetailsDTO());
        });
        
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userDetailsRepository, never()).save(any(UserDetails.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateUserDetails_WithNullValues() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(invocation -> {
            UserDetails savedDetails = invocation.getArgument(0);
            savedDetails.setUserDetailsId(1L);
            return savedDetails;
        });
        
        // Create a new DTO with null values
        UserDetailsDTO inputDTO = new UserDetailsDTO();
        // all values are null
        
        // Act
        UserDetailsDTO result = userDetailsService.createUserDetails(userId, inputDTO);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getUserDetailsId());
        assertEquals(0.0, result.getTotalDistance());
        assertEquals(0.0, result.getTotalDuration());
        assertEquals(0.0, result.getWeeklyDistance());
        assertEquals(0, result.getRunCount());
        
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).save(any(UserDetails.class));
        verify(userRepository).save(user);
    }
}