package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

import lombok.Data;

@Data
public class RunnerStatistics {

    private TrainingSession[] trainingSessions;
    private double[] standardizedTrainingSessions;
    private final StandardDistance standardDistance;

    private List<Double> highIntensitySessions = new ArrayList<>();
    private List<Double> averageIntensitySessions = new ArrayList<>();
    private List<Double> lowIntensitySessions = new ArrayList<>();

    private double fastCutoff; // Every time faster than this is considered high intensity
    private double slowCutoff; // Every time slower than this is considered low intensity
    private double meanTime;
    private double standardDeviation;

    // Deviation factor
    // A factor of 1 means that you roughly have 68% data in the middel (average run) and 16% on each side (high and low intensity)
    // We will likely want more data in each extremity, so we will set the factor to 0.7 for now
    double lowerDeviationFactor; 
    double upperDeviationFactor;

    // This is the important data we want (IMPORTANT)
    private double highIntensityMeanTime;
    private double averageIntensityMeanTime;
    private double lowIntensityMeanTime;

    // Default constructor if no deviation factors are provided
    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance) {
        this(trainingSessions, standardDistance, 0.7, 0.7);
    }

    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance, double lowerDeviationFactor, double upperDeviationFactor) {
        if (trainingSessions == null || trainingSessions.length == 0) {
            throw new IllegalArgumentException("Training sessions array cannot be null or empty");
        }
        
        this.trainingSessions = trainingSessions;
        this.standardDistance = standardDistance;
        this.lowerDeviationFactor = lowerDeviationFactor;
        this.upperDeviationFactor = upperDeviationFactor;

        // Standardized training sessions
        this.standardizedTrainingSessions = RiegelConverter.convertAll(this.trainingSessions, this.standardDistance);
        getPaceZones();
        setAllMeanTimes();
    }

    // Pace Zones
    private void getPaceZones() {     

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
                this.averageIntensitySessions.add(standardizedTime);
            }
        }
    }

    private void setAllMeanTimes() {
        this.highIntensityMeanTime = getMeanTime(this.highIntensitySessions);
        this.averageIntensityMeanTime = getMeanTime(this.averageIntensitySessions);
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
