package com.example.RunAgainstTheWind.algorithmTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.RunAgainstTheWind.algorithm.RunnerStatistics;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

class RunnerStatisticsTest {
    
    private List<TrainingSessionDTO> trainingSessions;
    
    @BeforeEach
    void setUp() {
        trainingSessions = new ArrayList<>();

    UUID userId = UUID.randomUUID(); // Replace with a test UUID if needed
    LocalDate today = LocalDate.now();

        trainingSessions.add(createTrainingSessionDTO(5000.0, 18.0, userId, today, 10)); // Fast 5K
        trainingSessions.add(createTrainingSessionDTO(5000.0, 19.0, userId, today, 9));  // Fast 5K
        trainingSessions.add(createTrainingSessionDTO(10000.0, 40.0, userId, today, 9)); // Fast 10K
        trainingSessions.add(createTrainingSessionDTO(5000.0, 22.0, userId, today, 6));  // Medium 5K
        trainingSessions.add(createTrainingSessionDTO(5000.0, 23.5, userId, today, 6));  // Medium 5K
        trainingSessions.add(createTrainingSessionDTO(8000.0, 36.0, userId, today, 6));  // Medium 8K
        trainingSessions.add(createTrainingSessionDTO(10000.0, 47.0, userId, today, 6)); // Medium 10K
        trainingSessions.add(createTrainingSessionDTO(5000.0, 26.0, userId, today, 4));  // Slow 5K
        trainingSessions.add(createTrainingSessionDTO(5000.0, 28.0, userId, today, 3));  // Slow 5K
        trainingSessions.add(createTrainingSessionDTO(3000.0, 18.0, userId, today, 2));
    }
    
    private TrainingSessionDTO createTrainingSessionDTO(Double distance, Double duration, UUID userId, LocalDate date, int effort) {
        double pace = duration / (distance / 1000.0); // pace in min/km
    
        return new TrainingSessionDTO(
            TrainingType.TEMPO, 
            date,
            distance,
            duration,
            pace,
            true,
            distance,
            duration,
            pace,
            effort,
            "", 
            null, 
            null, 
            userId
        );
    }
    
    @Test
    void testStatisticsCalculation() throws MissingDataException {

        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);
        
        // Test that mean and standard deviation are calculated correctly
        double[] standardizedTimes = stats.getStandardizedTrainingSessions();
        double expectedMean = Arrays.stream(standardizedTimes).average().orElse(0);
        double sumSquaredDiffs = Arrays.stream(standardizedTimes)
            .map(time -> Math.pow(time - expectedMean, 2))
            .sum();
        double expectedStdDev = Math.sqrt(sumSquaredDiffs / standardizedTimes.length);
        
        assertEquals(expectedMean, stats.getMeanTime(), 0.001, "Mean time should be calculated correctly");
        assertEquals(expectedStdDev, stats.getStandardDeviation(), 0.001, "Standard deviation should be calculated correctly");
        
        // Test that cutoffs are calculated correctly
        double expectedFastCutoff = expectedMean - (0.7 * expectedStdDev);
        double expectedSlowCutoff = expectedMean + (0.7 * expectedStdDev);
        
        assertEquals(expectedFastCutoff, stats.getFastCutoff(), 0.001, "Fast cutoff should be calculated correctly");
        assertEquals(expectedSlowCutoff, stats.getSlowCutoff(), 0.001, "Slow cutoff should be calculated correctly");
    }
    
    @Test
    void testPaceZoneDistribution() throws MissingDataException {
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);
        
        // We expect roughly 30% high intensity, 40% average intensity, 30% low intensity given 0.7 deviation factor
        assertEquals(3, stats.getHighIntensitySessions().size(), 
            "Should have 3 high intensity sessions");
        assertEquals(4, stats.getMediumIntensitySessions().size(), 
            "Should have 4 average intensity sessions");
        assertEquals(3, stats.getLowIntensitySessions().size(), 
            "Should have 3 low intensity sessions");
        
        // Check that total count matches
        assertEquals(trainingSessions.size(), 
            stats.getHighIntensitySessions().size() + 
            stats.getMediumIntensitySessions().size() + 
            stats.getLowIntensitySessions().size(),
            "Sum of all categorized sessions should equal total sessions");
    }
    
    @Test
    void testCustomDeviationFactors() throws MissingDataException {
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM, 1.0, 1.0);
        
        // Test with more extreme deviation factors (1.0) should categorize more runs as average
        // We expect more runs in the middle, fewer at the extremes
        assertTrue(stats.getHighIntensitySessions().size() <= 2, 
            "With narrow deviation bands, should have fewer high intensity sessions");
        assertTrue(stats.getLowIntensitySessions().size() <= 2, 
            "With narrow deviation bands, should have fewer low intensity sessions");
        assertTrue(stats.getMediumIntensitySessions().size() >= 6, 
            "With narrow deviation bands, should have more average intensity sessions");
    }
    
    @Test
    void testRiegelConversion() throws MissingDataException {

        // Actual
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);
        double[] standardized = stats.getStandardizedTrainingSessions();
        
        // Expected
        double expected10KNormalizedTo5K = trainingSessions.get(2).getAchievedDuration() * 
            Math.pow(StandardDistance.FIVE_KM.getKilometers() / trainingSessions.get(2).getAchievedDistance(), 1.06);
        
        // Compare
        assertEquals(expected10KNormalizedTo5K, standardized[2], 0.001, 
            "10K run should be correctly standardized to 5K equivalent");
    }
    
    @Test
    void testDifferentStandardDistances() throws MissingDataException {

        RunnerStatistics statsFor10K = new RunnerStatistics(trainingSessions, StandardDistance.TEN_KM);
        RunnerStatistics statsFor5K = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);

        // Cutoff times should be different
        assertNotEquals(statsFor5K.getFastCutoff(), statsFor10K.getFastCutoff(),
            "Fast cutoffs should differ when standardizing to different distances");
        assertNotEquals(statsFor5K.getSlowCutoff(), statsFor10K.getSlowCutoff(),
            "Slow cutoffs should differ when standardizing to different distances");
        
        // Relative distribution of sessions should be similar
        assertEquals(statsFor5K.getHighIntensitySessions().size(), statsFor10K.getHighIntensitySessions().size(),
            "Distribution should be similar regardless of standard distance");
    }
    
    @Test
    void testEmptyTrainingSessions() {
        List<TrainingSessionDTO> emptySessions = new ArrayList<>(); 
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new RunnerStatistics(emptySessions, StandardDistance.FIVE_KM);
        });
        
        assertTrue(exception.getMessage().contains("empty"),
                  "Should throw exception with 'empty' in the message for an empty sessions array");
    }
}