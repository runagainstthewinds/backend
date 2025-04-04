package com.example.RunAgainstTheWind.algorithmTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.RunAgainstTheWind.algorithm.RunnerStatistics;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

class RunnerStatisticsTest {
    
    private TrainingSession[] trainingSessions;
    
    @BeforeEach
    void setUp() {
        trainingSessions = new TrainingSession[10];
        
        // Fast 5K (18:00 - High intensity)
        // Converts to 18:00 for 5K
        trainingSessions[0] = createTrainingSession(5000, 18.0);
        
        // Fast 5K (19:00 - High intensity)
        // Converts to 19:00 for 5K
        trainingSessions[1] = createTrainingSession(5000, 19.0);
        
        // Fast 10K (40:00 - High intensity to 5K)
        // Converts to 19.11 for 5K
        trainingSessions[2] = createTrainingSession(10000, 40.0);
        
        // Medium 5K (22:00 - Average intensity)
        // Converts to 22:00 for 5K
        trainingSessions[3] = createTrainingSession(5000, 22.0);
        
        // Medium 5K (23:30 - Average intensity)
        // Converts to 23:30 for 5K
        trainingSessions[4] = createTrainingSession(5000, 23.5);
        
        // Medium 8K (36:00 - Average intensity)
        // Converts to 21:52 for 5K
        trainingSessions[5] = createTrainingSession(8000, 36.0);
        
        // Medium 10K (47:00 - Average intensity)
        // Converts to 22:32
        trainingSessions[6] = createTrainingSession(10000, 47.0);
        
        // Slow 5K (26:00 - Low intensity)
        // Converts to 26:00 for 5K
        trainingSessions[7] = createTrainingSession(5000, 26.0);
        
        // Slow 5K (28:00 - Low intensity)
        // Converts to 28:00 for 5K
        trainingSessions[8] = createTrainingSession(5000, 28.0);
        
        // Very slow 3K (18:00 - Low intensity)
        // Converts to 30:56 for 5K
        trainingSessions[9] = createTrainingSession(3000, 18.0);
    }
    
    private TrainingSession createTrainingSession(double distance, double duration) {
        TrainingSession session = new TrainingSession();
        session.setAchievedDistance(distance);
        session.setAchievedDuration(duration);
        return session;
    }
    
    @Test
    void testStatisticsCalculation() {

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
    void testPaceZoneDistribution() {
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);
        
        // We expect roughly 30% high intensity, 40% average intensity, 30% low intensity given 0.7 deviation factor
        assertEquals(3, stats.getHighIntensitySessions().size(), 
            "Should have 3 high intensity sessions");
        assertEquals(4, stats.getAverageIntensitySessions().size(), 
            "Should have 4 average intensity sessions");
        assertEquals(3, stats.getLowIntensitySessions().size(), 
            "Should have 3 low intensity sessions");
        
        // Check that total count matches
        assertEquals(trainingSessions.length, 
            stats.getHighIntensitySessions().size() + 
            stats.getAverageIntensitySessions().size() + 
            stats.getLowIntensitySessions().size(),
            "Sum of all categorized sessions should equal total sessions");
    }
    
    @Test
    void testCustomDeviationFactors() {
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM, 1.0, 1.0);
        
        // Test with more extreme deviation factors (1.0) should categorize more runs as average
        // We expect more runs in the middle, fewer at the extremes
        assertTrue(stats.getHighIntensitySessions().size() <= 2, 
            "With narrow deviation bands, should have fewer high intensity sessions");
        assertTrue(stats.getLowIntensitySessions().size() <= 2, 
            "With narrow deviation bands, should have fewer low intensity sessions");
        assertTrue(stats.getAverageIntensitySessions().size() >= 6, 
            "With narrow deviation bands, should have more average intensity sessions");
    }
    
    @Test
    void testRiegelConversion() {

        // Actual
        RunnerStatistics stats = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM);
        double[] standardized = stats.getStandardizedTrainingSessions();
        
        // Expected
        double expected10KNormalizedTo5K = trainingSessions[2].getAchievedDuration() * 
            Math.pow(StandardDistance.FIVE_KM.getMeters() / trainingSessions[2].getAchievedDistance(), 1.06);
        
        // Compare
        assertEquals(expected10KNormalizedTo5K, standardized[2], 0.001, 
            "10K run should be correctly standardized to 5K equivalent");
    }
    
    @Test
    void testDifferentStandardDistances() {

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
        TrainingSession[] emptySessions = new TrainingSession[0];
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new RunnerStatistics(emptySessions, StandardDistance.FIVE_KM);
        });
        
        assertTrue(exception.getMessage().contains("empty"),
                  "Should throw exception with 'empty' in the message for an empty sessions array");
    }
}