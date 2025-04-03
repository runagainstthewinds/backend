package com.example.RunAgainstTheWind.algorithmTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.RunAgainstTheWind.algorithm.RiegelConverter;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

public class RiegelConverterTest {
    private TrainingSession[] trainingSessions;

    @BeforeEach
    public void setUp() {
        trainingSessions = new TrainingSession[] {
            // Date, distance(m), duration(s), goalPace, isCompleted, achievedPace, achievedDistance, achievedDuration, effort
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 10000.0, 40.0, 0
            ),
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 3000.0, 15.0, 0
            ),
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 1000.0, 6.0, 0
            )
        };
    }

    @Test
    public void testPredicted5KTimes() {

        double[] predicted5KTimes = RiegelConverter.convertAll(trainingSessions, StandardDistance.FIVE_KM);
        assertEquals(3, predicted5KTimes.length, "Should have predictions for all 3 training sessions");

        double[] expectedTimes = new double[] {
            19.185,  
            25.778, 
            33.041  
        };

        double delta = 0.001; // Acceptable margin of error
        for (int i = 0; i < predicted5KTimes.length; i++) {
            assertEquals(
                expectedTimes[i], 
                predicted5KTimes[i], 
                delta,
                String.format("Prediction for session %d (%.1fm in %.1fs) should match expected 5K time", 
                    i, 
                    trainingSessions[i].getAchievedDistance(), 
                    trainingSessions[i].getAchievedDuration())
            );
        }
    }

    @Test
    public void testEmptySessionsArray() {
        TrainingSession[] emptySessions = new TrainingSession[0];
        double[] predictions = RiegelConverter.convertAll(emptySessions, StandardDistance.FIVE_KM);
        assertEquals(0, predictions.length, "Empty input array should return empty predictions array");
    }

    @Test
    public void testSingleSession() {
        TrainingSession[] singleSession = new TrainingSession[] {
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 10000.0, 40.0, 0
            )
        };
        double[] predictions = RiegelConverter.convertAll(singleSession, StandardDistance.FIVE_KM);
        assertEquals(1, predictions.length, "Single session should return one prediction");
        assertEquals(19.185, predictions[0], 0.001, "Single session prediction should match expected value");
    }
}
