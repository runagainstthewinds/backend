package com.example.RunAgainstTheWind.algorithm;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

public class RiegelConverter {
    // Riegel's fatigue factor (usually 1.06)
    private static final double FATIGUE_FACTOR = 1.06;

    // Standard distance will be 5K
    private static final double STANDARD_DISTANCE = 5000;

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
     * Converts a training session to predicted 5K time
     * @param trainingSession The training session with distance (meters) and time (seconds)
     * @return Predicted 5K time in seconds
     */
    public static double convertTo5K(TrainingSession trainingSession) {
        return predictTime(trainingSession.getAchievedDistance(), trainingSession.getAchievedDuration(), STANDARD_DISTANCE);
    }

    /**
     * Converts a list of training sessions to predicted 5K times
     * @param sessions List of training sessions
     * @return Array of predicted 5K times in seconds
     */
    public static double[] convertAllTo5K(TrainingSession[] trainingSessions) {
        double[] predictedTimes = new double[trainingSessions.length];

        for (int i = 0; i< trainingSessions.length; i++) {
            predictedTimes[i] = convertTo5K(trainingSessions[i]);
        }

        return predictedTimes;
    }
}
