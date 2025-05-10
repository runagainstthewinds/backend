package com.example.RunAgainstTheWind.algorithm;

import java.util.List;

import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

/**
 * A utility class that implements Riegel's formula to predict running times for different distances
 * based on known performances. The formula accounts for fatigue to estimate equivalent performance times.
 */
public class RiegelConverter {

    private static final double RIEGEL_FATIGUE_FACTOR = 1.06; // Riegel's fatigue factor

    /**
     * Predicts a time for a target distance based on a known performance
     * @param knownDistance Distance in meters
     * @param knownTime Time in minutes and decimal seconds
     * @param targetDistance Target distance in meters
     * @return Predicted time in minutes and decimal seconds
     */
    public static double predictTime(double knownDistance, double knownTime, double targetDistance) {
        if (knownDistance <= 0 || knownTime <= 0 || targetDistance <= 0) throw new IllegalArgumentException("Distances and time must be positive");
        
        return knownTime * Math.pow(targetDistance / knownDistance, RIEGEL_FATIGUE_FACTOR);
    }

    /**
     * Converts a list of training sessions to predicted 5K times
     * @param sessions List of training sessions
     * @param standardDistance Standard distance to convert to
     * @return Array of predicted times in seconds for standard distance
     */
    public static double[] convertAllRunsToStandardDistance(List<TrainingSessionDTO> trainingSessions, StandardDistance standardDistance) {
        if (trainingSessions == null || trainingSessions.isEmpty() || standardDistance == null) throw new IllegalArgumentException("Invalid input");
        

        double[] standardizedTimes = new double[trainingSessions.size()];
        for (int i = 0; i< trainingSessions.size(); i++) {
            standardizedTimes[i] = predictTime(trainingSessions.get(i).getAchievedDistance(), trainingSessions.get(i).getAchievedDuration(), standardDistance.getMeters());
        }

        return standardizedTimes;
    }
}
