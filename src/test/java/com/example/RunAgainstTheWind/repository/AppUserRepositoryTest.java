package com.example.RunAgainstTheWind.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUserRole;
import com.example.RunAgainstTheWind.domain.appUser.repository.AppUserRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;

@SpringBootTest
@Transactional 
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    public void setUp() {
        trainingSessionRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void testCreateAppUser() {
        // Create new AppUser
        AppUser appUser = new AppUser("David", "Zhou", "david.zhou3@mail.mcgill.ca", "12345678", AppUserRole.USER);
        AppUser savedAppUser = appUserRepository.save(appUser);

        // Check if it exists
        assertNotNull(savedAppUser.getId());

        // Check if it is retrievable from the database
        Optional<AppUser> retrievedAppUser = appUserRepository.findById(savedAppUser.getId());
        assertNotNull(retrievedAppUser);

        // Check if the attributes are correct
        assertEquals("David", retrievedAppUser.get().getFirstName());
        assertEquals("Zhou", retrievedAppUser.get().getLastName());
        assertEquals("david.zhou3@mail.mcgill.ca", retrievedAppUser.get().getUsername());
        assertEquals("12345678", retrievedAppUser.get().getPassword());
        assertEquals(AppUserRole.USER, retrievedAppUser.get().getAppUserRole());
        assertTrue(retrievedAppUser.get().isEnabled());
    }

    @Test
    public void testFindByEmail() {
        // Create and save an AppUser
        AppUser appUser = new AppUser("Jane", "Doe", "jane.doe@example.com", "password123", AppUserRole.ADMIN);
        appUserRepository.save(appUser);

        // Find by email
        Optional<AppUser> foundAppUser = appUserRepository.findByEmail("jane.doe@example.com");

        // Verify
        assertTrue(foundAppUser.isPresent());
        assertEquals("Jane", foundAppUser.get().getFirstName());
        assertEquals("Doe", foundAppUser.get().getLastName());
        assertEquals("jane.doe@example.com", foundAppUser.get().getUsername());
        assertEquals("password123", foundAppUser.get().getPassword());
        assertEquals(AppUserRole.ADMIN, foundAppUser.get().getAppUserRole());
    }

    @Test
    public void testFindByEmail_NotFound() {
        // Ensure no user exists with this email
        Optional<AppUser> foundAppUser = appUserRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundAppUser.isPresent());
    }

    @Test
    public void testDeleteAll() {
        // Create and save multiple AppUsers
        AppUser user1 = new AppUser("Alice", "Smith", "alice@example.com", "pass1", AppUserRole.USER);
        AppUser user2 = new AppUser("Bob", "Jones", "bob@example.com", "pass2", AppUserRole.USER);
        appUserRepository.save(user1);
        appUserRepository.save(user2);

        // Verify they exist
        assertEquals(2, appUserRepository.count());

        // Delete all
        appUserRepository.deleteAll();

        // Verify deletion
        assertEquals(0, appUserRepository.count());
    }
}