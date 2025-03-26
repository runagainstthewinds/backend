package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;
import com.example.RunAgainstTheWind.domain.trainingPlan.repository.TrainingPlanRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;
import com.example.RunAgainstTheWind.enumeration.Road;

@SpringBootTest
@Transactional
public class TrainingPlanRepositoryTest {
    
    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    public void setUp() {
        trainingSessionRepository.deleteAll();
        trainingPlanRepository.deleteAll();
    }

    @Test
    public void testCreateTrainingPlan() {
        // Create new training plan
        LocalDate localDate = LocalDate.of(2025, 3, 24);
        LocalDate localDate2 = LocalDate.of(2025, 4, 24);
        Date startDate = Date.valueOf(localDate);
        Date endDate = Date.valueOf(localDate2);
        TrainingPlan trainingPlan = new TrainingPlan(startDate, endDate, "Marathon", Road.GRASS , 42.2, 180.0);
        trainingPlanRepository.save(trainingPlan);

        // Check if it exists
        assertNotNull(trainingPlan.getTrainingPlanId());

        // Check if it is retrievable from the database
        TrainingPlan retrievedTrainingPlan = trainingPlanRepository.findById(trainingPlan.getTrainingPlanId()).get();
        assertNotNull(retrievedTrainingPlan);

        // Check if the attributes are correct
        assertEquals(startDate, retrievedTrainingPlan.getStartDate());
        assertEquals(endDate, retrievedTrainingPlan.getEndDate());
        assertEquals("Marathon", retrievedTrainingPlan.getPlanType());
        assertEquals(Road.GRASS, retrievedTrainingPlan.getRoadType());
        assertEquals(42.2, retrievedTrainingPlan.getGoalDistance());
        assertEquals(180.0, retrievedTrainingPlan.getGoalTime());
    }
}
