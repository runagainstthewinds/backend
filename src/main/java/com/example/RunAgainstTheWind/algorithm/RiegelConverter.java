package com.example.RunAgainstTheWind.algorithm;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

public class RiegelConverter {
    // Riegel's fatigue factor (usually 1.06)
    private static final double FATIGUE_FACTOR = 1.06;

    /**
     * Predicts a time for a target distance based on a known performance
     * @param knownDistance Distance in meters
     * @param knownTime Time in minutes and decimal seconds
     * @param targetDistance Target distance in meters
     * @return Predicted time in minutes and decimal seconds
     */
    public static double predictTime(double knownDistance, double knownTime, double targetDistance) {
        return knownTime * Math.pow(targetDistance / knownDistance, FATIGUE_FACTOR);
    }

    /**
     * Converts a training session to predicted time for given standard distance
     * @param trainingSession The training session with distance (meters) and time (seconds)
     * @return Predicted time in seconds
     */
    private static double convertToStandard(TrainingSession trainingSession, StandardDistance standardDistance) {
        return predictTime(trainingSession.getAchievedDistance(), trainingSession.getAchievedDuration(), standardDistance.getMeters());
    }

    /**
     * Converts a list of training sessions to predicted 5K times
     * @param sessions List of training sessions
     * @return Array of predicted times in seconds for standard distance
     */
    public static double[] convertAll(TrainingSession[] trainingSessions, StandardDistance standardDistance) {
        double[] predictedTimes = new double[trainingSessions.length];

        for (int i = 0; i< trainingSessions.length; i++) {
            predictedTimes[i] = convertToStandard(trainingSessions[i], standardDistance);
        }

        return predictedTimes;
    }
}
