package com.example.RunAgainstTheWind.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUserRole;
import com.example.RunAgainstTheWind.domain.appUser.repository.AppUserRepository;
import com.example.RunAgainstTheWind.domain.road.model.Road;
import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;

@SpringBootTest
@Transactional
public class TrainingSessionRepositoryTest {
    
    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @BeforeEach
    public void setUp() {
        trainingSessionRepository.deleteAll();
    }

    @Test
    public void testCreateTrainingSession() {
        // Create new training session
        LocalDate localTrainingDate = LocalDate.of(2025, 3, 24);
        Date trainingDate = Date.valueOf(localTrainingDate);

        Shoe shoe = new Shoe("Nike", "Alphafly", 10.0, 100.0, 250.0);
        shoe = shoeRepository.save(shoe);

        AppUser appUser = new AppUser("David", "Zhou","david.zhou3@mail.mcgill.ca", "12345678", AppUserRole.USER);
        appUser = appUserRepository.save(appUser);

        LocalDate localDate = LocalDate.of(2025, 3, 24);
        LocalDate localDate2 = LocalDate.of(2025, 4, 24);
        Date startDate = Date.valueOf(localDate);
        Date endDate = Date.valueOf(localDate2);
        TrainingPlan trainingPlan = new TrainingPlan(startDate, endDate, "Marathon", Road.GRASS , 42.2, 180.0);
        trainingPlan = trainingPlanRepository.save(trainingPlan);

        TrainingSession trainingSession = new TrainingSession(trainingDate, 10.0, 60.0, 6.0, false, 5.0, 10.0, 60.0, 5.0, shoe, appUser, trainingPlan);
        trainingSessionRepository.save(trainingSession);

        // Check if it exists
        assertNotNull(trainingSession.getTrainingSessionId());

        // Check if it is retrievable from the database
        TrainingSession retrievedTrainingSession = trainingSessionRepository.findById(trainingSession.getTrainingSessionId()).get();
        assertNotNull(retrievedTrainingSession);

        // Check if the attributes are correct
        assertEquals(startDate, retrievedTrainingSession.getDate());
        assertEquals(10.0, retrievedTrainingSession.getDistance());
        assertEquals(60.0, retrievedTrainingSession.getDuration());
        assertEquals(6.0, retrievedTrainingSession.getGoalPace());
        assertEquals(false, retrievedTrainingSession.getIsCompleted());
        assertEquals(5.0, retrievedTrainingSession.getAchievedPace());
        assertEquals(10.0, retrievedTrainingSession.getAchievedDistance());
        assertEquals(60.0, retrievedTrainingSession.getAchievedDuration());
        assertEquals(5.0, retrievedTrainingSession.getEffort());
        assertEquals("Nike", retrievedTrainingSession.getShoe().getBrand());
        assertEquals("David", retrievedTrainingSession.getAppUser().getFirstName());
        assertEquals("Marathon", retrievedTrainingSession.getTrainingPlan().getPlanType());
    }
}
