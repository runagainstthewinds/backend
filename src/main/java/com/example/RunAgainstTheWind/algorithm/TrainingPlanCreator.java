package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

import lombok.Data;

// INPUT: 
// Training plan difficulty (Easy, Medium, Hard), Program length (week), Distance (m), RunnerStatistics
// OUTPUT: Training plan mapping {week:sessions}

@Data
public class TrainingPlanCreator {

    // INPUTS
    private TrainingSession[] runHistory;
    private String difficulty;
    private int length; // weeks
    private int goalDistance; // m 
    private StandardDistance standardDistance = StandardDistance.FIVE_KM; 

    private RunnerStatistics runnerStatistics;
    private TrainingPlanSkeleton trainingPlanSkeleton;

    private int tempoCount = 0;
    private int longRunCount = 0;
    private int intervalCount = 0;
    private int recoveryRunCount = 0;

    public TrainingPlanCreator(TrainingSession[] runHistory, String difficulty, int length, int goalDistance) {
        this.runHistory = runHistory;
        this.difficulty = difficulty;
        this.length = length;
        this.goalDistance = goalDistance;

        this.runnerStatistics = new RunnerStatistics(this.runHistory, StandardDistance.FIVE_KM, 1.0, 1.0);
        this.trainingPlanSkeleton = new TrainingPlanSkeleton(this.difficulty, this.length);

        countTrainingTypes();
        setLongRun();
    }

    private void countTrainingTypes() {
        // Count the number of each training type in the run history
        for (List<TrainingSession> sessions : this.trainingPlanSkeleton.getPlan().values()) {
            for (TrainingSession session : sessions) {
                if (session != null) {
                    TrainingType type = session.getTrainingType();
                    if (type == TrainingType.TEMPO) {
                        tempoCount++;
                    } else if (type == TrainingType.LONG_RUN) {
                        longRunCount++;
                    } else if (type == TrainingType.INTERVAL) {
                        intervalCount++;
                    } else if (type == TrainingType.RECOVERY_RUN) {
                        recoveryRunCount++;
                    }
                }
            }
        }

        // Print counts for debugging or further logic
        System.out.println("Tempo sessions: " + tempoCount);
        System.out.println("Long Run sessions: " + longRunCount);
        System.out.println("Interval sessions: " + intervalCount);
        System.out.println("Recovery Run sessions: " + recoveryRunCount);
    }

    // Distance: Starts at 50% of goal distance. Increases to 100%
    // Duration: Low intensity pace

    private void setLongRun() {
        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();

        Map<Integer, List<TrainingSession>> plan = trainingPlanSkeleton.getPlan();
        
        // Get all long run sessions
        List<TrainingSession> longRunSessions = new ArrayList<>();
        for (List<TrainingSession> sessions : plan.values()) {
            for (TrainingSession session : sessions) {
                if (session != null && session.getTrainingType() == TrainingType.LONG_RUN) {
                    longRunSessions.add(session);
                }
            }
        }

        if (this.longRunCount > 0) {
            double startDistance = goalDistance * 0.5; // First long run: 50% of goalDistance
            double endDistance = goalDistance;        // Last long run: goalDistance
            double increment = this.longRunCount > 1 ? 
                (endDistance - startDistance) / (this.longRunCount - 1) : 
                0.0;
    
            for (int i = 0; i < this.longRunCount; i++) {
                double distance = startDistance + (i * increment);
                longRunSessions.get(i).setDistance(distance);

                double duration = RiegelConverter.predictTime(this.standardDistance.getMeters(), lowIntensityMeanTime, distance);
                longRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);

                double pace = duration / (distance / 1000);
                longRunSessions.get(i).setGoalPace(Math.round(pace * 100.0) / 100.0);
            }
        }
    }

    private void setTempoRun() {

    }

    private void setIntervalRun() {

    }

    private void setRecoveryRun() { 
    
    }
}
