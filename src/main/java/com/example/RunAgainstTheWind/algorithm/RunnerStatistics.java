package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

import lombok.Getter;

/**
 * Calculates and categorizes running session statistics based on pace zones.
 * Sessions are standardized to a given distance and classified into high, medium, and low intensity.
 */
@Getter
public class RunnerStatistics {

    private static final double DEFAULT_DEVIATION_FACTOR = 0.7; 

    private final TrainingSession[] trainingSessions;
    private double[] standardizedTrainingSessions;
    private final StandardDistance standardDistance;
    private final double lowerDeviationFactor;   // Smaller value mean more data in extremeties
    private final double upperDeviationFactor;

    private List<Double> highIntensitySessions;
    private List<Double> mediumIntensitySessions;
    private List<Double> lowIntensitySessions;

    private double fastCutoff; 
    private double slowCutoff; 
    private double meanTime;
    private double standardDeviation;
    
    private double highIntensityMeanTime;
    private double mediumIntensityMeanTime;
    private double lowIntensityMeanTime;

    // Default constructor if no deviation factors are provided
    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance) throws MissingDataException {
        this(trainingSessions, standardDistance, DEFAULT_DEVIATION_FACTOR, DEFAULT_DEVIATION_FACTOR);
    }

    /**
     * Constructs a RunnerStatistics instance with custom deviation factors.
     *
     * @param trainingSessions      Array of training sessions
     * @param standardDistance      The standard distance for normalization
     * @param lowerDeviationFactor  Factor for high-intensity cutoff
     * @param upperDeviationFactor  Factor for low-intensity cutoff
     */
    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance,
                            double lowerDeviationFactor, double upperDeviationFactor)
            throws MissingDataException {
        validateInputs(trainingSessions, standardDistance, lowerDeviationFactor, upperDeviationFactor);

        this.trainingSessions = trainingSessions;
        this.standardDistance = standardDistance;
        this.lowerDeviationFactor = lowerDeviationFactor;
        this.upperDeviationFactor = upperDeviationFactor;

        // Standardize sessions
        this.standardizedTrainingSessions = RiegelConverter.convertAllRunsToStandardDistance(
                trainingSessions, standardDistance);

        // Calculate statistics
        this.highIntensitySessions = new ArrayList<>();
        this.mediumIntensitySessions = new ArrayList<>();
        this.lowIntensitySessions = new ArrayList<>();
        this.meanTime = calculateMeanTime();
        this.standardDeviation = calculateStandardDeviation();
        this.fastCutoff = meanTime - (lowerDeviationFactor * standardDeviation);
        this.slowCutoff = meanTime + (upperDeviationFactor * standardDeviation);

        categorizeSessions();
        this.highIntensityMeanTime = calculateMeanTime(highIntensitySessions, "high-intensity");
        this.mediumIntensityMeanTime = calculateMeanTime(mediumIntensitySessions, "medium-intensity");
        this.lowIntensityMeanTime = calculateMeanTime(lowIntensitySessions, "low-intensity");
    }

    /**
     * Validates constructor inputs.
     */
    private void validateInputs(TrainingSession[] sessions, StandardDistance distance,
                               double lowerFactor, double upperFactor) {
        if (sessions == null || sessions.length == 0) {
            throw new IllegalArgumentException("Training sessions array cannot be null or empty");
        }
        for (TrainingSession session : sessions) {
            if (session == null) {
                throw new IllegalArgumentException("Training session cannot be null");
            }
        }
        if (distance == null) {
            throw new IllegalArgumentException("Standard distance cannot be null");
        }
        if (lowerFactor <= 0 || upperFactor <= 0) {
            throw new IllegalArgumentException("Deviation factors must be positive");
        }
    }

    /**
     * Calculates the mean time of standardized sessions.
     */
    private double calculateMeanTime() {
        return Arrays.stream(standardizedTrainingSessions)
                     .average()
                     .orElseThrow(() -> new IllegalStateException("Failed to compute mean time"));
    }

    /**
     * Calculates the standard deviation of standardized sessions.
     */
    private double calculateStandardDeviation() {
        return Math.sqrt(
                Arrays.stream(standardizedTrainingSessions)
                      .map(time -> Math.pow(time - meanTime, 2))
                      .average()
                      .orElse(0.0)
        );
    }

    /**
     * Categorizes sessions into high, medium, and low intensity based on cutoffs.
     */
    private void categorizeSessions() {
        for (double time : standardizedTrainingSessions) {
            if (time < fastCutoff) {
                highIntensitySessions.add(time);
            } else if (time > slowCutoff) {
                lowIntensitySessions.add(time);
            } else {
                mediumIntensitySessions.add(time);
            }
        }

        if (highIntensitySessions.isEmpty() || mediumIntensitySessions.isEmpty() || lowIntensitySessions.isEmpty()) {
            throw new MissingDataException("Not all intensity levels have sessions: " +
                    "High=" + highIntensitySessions.size() +
                    ", Medium=" + mediumIntensitySessions.size() +
                    ", Low=" + lowIntensitySessions.size());
        }
    }

    /**
     * Calculates the mean time for a list of sessions.
     *
     * @param sessions    List of session times
     * @param intensity   Intensity level for error messaging
     * @return Mean time
     * @throws MissingDataException if the session list is empty
     */
    private double calculateMeanTime(List<Double> sessions, String intensity) throws MissingDataException {
        if (sessions.isEmpty()) {
            throw new MissingDataException("No " + intensity + " sessions available");
        }
        return sessions.stream()
                       .mapToDouble(Double::doubleValue)
                       .average()
                       .orElseThrow(() -> new IllegalStateException("Failed to compute mean for " + intensity));
    }

    // Unmodifiable getters
    public List<Double> getHighIntensitySessions() {
        return Collections.unmodifiableList(highIntensitySessions);
    }

    public List<Double> getMediumIntensitySessions() {
        return Collections.unmodifiableList(mediumIntensitySessions);
    }

    public List<Double> getLowIntensitySessions() {
        return Collections.unmodifiableList(lowIntensitySessions);
    }
}
