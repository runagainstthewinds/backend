package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.domain.userDetails.repository.UserDetailsRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserDetailsRepositoryTest {
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @BeforeEach
    public void setUp() {
        userDetailsRepository.deleteAll();
    }

    @Test
    public void testCreateUserDetails() {
        // Create a new UserDetails object
        UserDetails userDetails = new UserDetails(10.0, 2.0, 1.0, 2);
        UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
        
        // Check it exists
        assertNotNull(savedUserDetails.getUserDetailsId());

        // Check it is retrievable from databse
        Optional<UserDetails> retrievedUserDetails = userDetailsRepository.findById(savedUserDetails.getUserDetailsId());
        assertNotNull(retrievedUserDetails);

        // Check the attributes are correct
        assertNotNull(retrievedUserDetails.get().getTotalDistance());
        assertNotNull(retrievedUserDetails.get().getTotalDuration());
        assertNotNull(retrievedUserDetails.get().getWeeklyDistance());
        assertNotNull(retrievedUserDetails.get().getRunCount());
        assertEquals(10.0, retrievedUserDetails.get().getTotalDistance());
        assertEquals(2.0, retrievedUserDetails.get().getTotalDuration());
        assertEquals(1.0, retrievedUserDetails.get().getWeeklyDistance());
        assertEquals(2, retrievedUserDetails.get().getRunCount());
    }
}
