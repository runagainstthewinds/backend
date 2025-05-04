package com.example.RunAgainstTheWind.algorithmTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.RunAgainstTheWind.algorithm.RiegelConverter;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

public class RiegelConverterTest {
    private TrainingSession[] trainingSessions;

    @BeforeEach
    public void setUp() {
        trainingSessions = new TrainingSession[] {
            // Date, distance(m), duration(s), goalPace, isCompleted, achievedPace, achievedDistance, achievedDuration, effort
            new TrainingSession(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 10000.0, 40.0, 0.0, 0, "", null, null, null
            ),
            new TrainingSession(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 3000.0, 15.0, 0.0, 0, "", null, null, null
            ),
            new TrainingSession(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 1000.0, 6.0, 0.0, 0, "", null, null, null
            )
        };
    }

    @Test
    public void testPredicted5KTimes() {

        double[] predicted5KTimes = RiegelConverter.convertAllRunsToStandardDistance(trainingSessions, StandardDistance.FIVE_KM);
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
        assertThrows(IllegalArgumentException.class, () -> {
            RiegelConverter.convertAllRunsToStandardDistance(emptySessions, StandardDistance.FIVE_KM);
        }, "Empty input array should throw IllegalArgumentException");
    }

    @Test
    public void testSingleSession() {
        TrainingSession[] singleSession = new TrainingSession[] {
            new TrainingSession(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 10000.0, 40.0, 0.0, 0, "", null, null, null
            )
        };
        double[] predictions = RiegelConverter.convertAllRunsToStandardDistance(singleSession, StandardDistance.FIVE_KM);
        assertEquals(1, predictions.length, "Single session should return one prediction");
        assertEquals(19.185, predictions[0], 0.001, "Single session prediction should match expected value");
    }
}
