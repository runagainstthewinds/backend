package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;

public class RunnerStatistics {

    private TrainingSession[] trainingSessions;
    private double[] standardizedTrainingSessions;
    private final StandardDistance standardDistance;

    private List<Double> highIntensitySessions;
    private List<Double> averageInstensitySessions;
    private List<Double> lowIntensitySessions;

    private double fastCutoff; // Every time faster than this is considered high intensity
    private double slowCutoff; // Every time slower than this is considered low intensity
    private double meanTime;
    private double standardDeviation;

    // If we knew what the "average" runner's distribution for session intensity looked like, we could use these factors to take that into account.
    // For example, if I knew 80% of runners usually follow a distribution of about 20% high, 60% average, and 20% low intensity sessions, I could use that to set the cutoffs.
    // For now, they will be passed in as 0.
    double lowerDeviationFactor; 
    double upperDeviationFactor;

    public RunnerStatistics(TrainingSession[] trainingSessions, StandardDistance standardDistance, double lowerDeviationFactor, double upperDeviationFactor) {
        this.trainingSessions = trainingSessions;
        this.standardDistance = standardDistance;
        this.lowerDeviationFactor = lowerDeviationFactor;
        this.upperDeviationFactor = upperDeviationFactor;

        // Standardized training sessions
        this.standardizedTrainingSessions = RiegelConverter.convertAll(this.trainingSessions, this.standardDistance);
    }

    // Pace Zones
    public void getPaceZones() {     

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

        this.highIntensitySessions = new ArrayList<>();
        this.averageInstensitySessions = new ArrayList<>();
        this.lowIntensitySessions = new ArrayList<>();

        for (int i = 0; i < this.standardizedTrainingSessions.length; i++) {
            double standardizedTime = this.standardizedTrainingSessions[i];
            if (standardizedTime < fastCutoff) {
                this.highIntensitySessions.add(standardizedTime);
            } else if (standardizedTime > slowCutoff) {
                this.lowIntensitySessions.add(standardizedTime);
            } else {
                this.averageInstensitySessions.add(standardizedTime);
            }
        }
    }
}
