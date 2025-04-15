package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

import lombok.Data;

/**
 * Calculates and categorizes running session statistics based on pace zones.
 * Sessions are standardized to a given distance and classified into high, medium, and low intensity.
 */
@Data
public class RunnerStatistics {

    private static final double DEFAULT_DEVIATION_FACTOR = 0.7; 

    private final TrainingSession[] trainingSessions;
    private double[] standardizedTrainingSessions;
    private final StandardDistance standardDistance;
    private final double lowerDeviationFactor;   // Smaller value mean more data in extremeties
    private final double upperDeviationFactor;

    private List<Double> highIntensitySessions = new ArrayList<>();
    private List<Double> mediumIntensitySessions = new ArrayList<>();
    private List<Double> lowIntensitySessions = new ArrayList<>();

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
     * @throws IllegalArgumentException if inputs are invalid
     * @throws MissingDataException if sessions cannot be categorized into all intensity levels
     */
    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance, double lowerDeviationFactor, double upperDeviationFactor) throws MissingDataException {
        if (trainingSessions == null || trainingSessions.length == 0) {
            throw new IllegalArgumentException("Training sessions array cannot be null or empty");
        }
        
        this.trainingSessions = trainingSessions;
        this.standardDistance = standardDistance;
        this.lowerDeviationFactor = lowerDeviationFactor;
        this.upperDeviationFactor = upperDeviationFactor;

        // Standardized training sessions
        this.standardizedTrainingSessions = RiegelConverter.convertAllRunsToStandardDistance(this.trainingSessions, this.standardDistance);
        calculatePaceZones();
        setAllMeanTimes();
    }

    // Pace Zones
    private void calculatePaceZones() {   
        
        // Mean
        double sum = 0.0;
        for (double time : this.standardizedTrainingSessions) {
            sum += time;
        }
        this.meanTime = sum / this.standardizedTrainingSessions.length;

        // Standard Deviation
        double sumSquaredDif = 0.0;
        for (double time : this.standardizedTrainingSessions) {
            sumSquaredDif += Math.pow(time - this.meanTime, 2);
        }
        this.standardDeviation = Math.sqrt(sumSquaredDif / this.standardizedTrainingSessions.length);

        // Pace cutoffs
        this.fastCutoff = this.meanTime - (this.lowerDeviationFactor * this.standardDeviation);
        this.slowCutoff = this.meanTime + (this.upperDeviationFactor * this.standardDeviation);

        for (int i = 0; i < this.standardizedTrainingSessions.length; i++) {
            double standardizedTime = this.standardizedTrainingSessions[i];
            if (standardizedTime < fastCutoff) {
                this.highIntensitySessions.add(standardizedTime);
            } else if (standardizedTime > slowCutoff) {
                this.lowIntensitySessions.add(standardizedTime);
            } else {
                this.mediumIntensitySessions.add(standardizedTime);
            }
        }
    }

    private void setAllMeanTimes() throws MissingDataException {
        if (this.highIntensitySessions.isEmpty() || this.mediumIntensitySessions.isEmpty() || this.lowIntensitySessions.isEmpty()) {
            throw new MissingDataException("Sessions have not been correctly categorized into three levels");
        }

        this.highIntensityMeanTime = getMeanTime(this.highIntensitySessions);
        this.mediumIntensityMeanTime = getMeanTime(this.mediumIntensitySessions);
        this.lowIntensityMeanTime = getMeanTime(this.lowIntensitySessions);
    }

    private double getMeanTime(List<Double> sessions) {
        double sum = 0.0;
        for (double time : sessions) {
            sum += time;
        }
        return sum / sessions.size();
    }
}
