package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.appUserDetails.model.AppUserDetails;
import com.example.RunAgainstTheWind.domain.appUserDetails.repository.AppUserDetailsRepository;

@SpringBootTest
@Transactional
public class AppUserDetailsRepositoryTest {
    
    @Autowired
    private AppUserDetailsRepository appUserDetailsRepository;

    @BeforeEach
    public void setUp() {
        appUserDetailsRepository.deleteAll();
    }

    @Test
    public void testCreateAppUserDetails() {
        // Create a new AppUserDetails object
        AppUserDetails appUserDetails = new AppUserDetails(10.0, 2.0, 1.0, 2);
        AppUserDetails savedAppUserDetails = appUserDetailsRepository.save(appUserDetails);
        
        // Check it exists
        assertNotNull(savedAppUserDetails.getAppUserDetailsId());

        // Check it is retrievable from databse
        Optional<AppUserDetails> retrievedAppUserDetails = appUserDetailsRepository.findById(savedAppUserDetails.getAppUserDetailsId());
        assertNotNull(retrievedAppUserDetails);

        // Check the attributes are correct
        assertNotNull(retrievedAppUserDetails.get().getTotalDistance());
        assertNotNull(retrievedAppUserDetails.get().getTotalDuration());
        assertNotNull(retrievedAppUserDetails.get().getWeeklyDistance());
        assertNotNull(retrievedAppUserDetails.get().getRunCount());
        assertEquals(10.0, retrievedAppUserDetails.get().getTotalDistance());
        assertEquals(2.0, retrievedAppUserDetails.get().getTotalDuration());
        assertEquals(1.0, retrievedAppUserDetails.get().getWeeklyDistance());
        assertEquals(2, retrievedAppUserDetails.get().getRunCount());
    }
}
