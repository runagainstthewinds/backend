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
        setTempoRun();
        setRecoveryRun();
        setIntervalRun();
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

    // Distance: Distance always at 40% of goal distance
    // Duration: Lower duration each week, so pace increases

    private void setTempoRun() {
        double mediumIntensityMeanTime = this.runnerStatistics.getMediumIntensityMeanTime();

        Map<Integer, List<TrainingSession>> plan = trainingPlanSkeleton.getPlan();
        
        // Get all tempo run sessions
        List<TrainingSession> tempoRunSessions = new ArrayList<>();
        for (List<TrainingSession> sessions : plan.values()) {
            for (TrainingSession session : sessions) {
                if (session != null && session.getTrainingType() == TrainingType.TEMPO) {
                    tempoRunSessions.add(session);
                }
            }
        }

        if (this.tempoCount > 0) {
            double distance = goalDistance * 0.4; // 40%

            // Duration progression: from 110% to 90% of mediumIntensityMeanTime
            double startDurationFactor = 1.0; // 100%
            double endDurationFactor = 0.90;   // 90%
            double durationIncrement = this.tempoCount > 1 ?
                (endDurationFactor - startDurationFactor) / (this.tempoCount - 1) :
                0.0;

            for (int i = 0; i < this.tempoCount; i++) {
                // Calculate duration factor for this session
                double durationFactor = startDurationFactor + (i * durationIncrement);
                double duration = mediumIntensityMeanTime * durationFactor;

                // Set session attributes
                tempoRunSessions.get(i).setDistance(distance);
                tempoRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);

                // Calculate pace
                double pace = duration / (distance / 1000);
                tempoRunSessions.get(i).setGoalPace(Math.round(pace * 100.0) / 100.0);
            }
        }
    }

    // General: We do intervals of 400 meters. We increase # of sets each week
    // Distance: Start of with X sets of 400m where total is around 25% of goal distance. We finish where total is around 50%.
    // Duration: High intensity pace

    private void setIntervalRun() {
        double highIntensityMeanTime = this.runnerStatistics.getHighIntensityMeanTime();

        Map<Integer, List<TrainingSession>> plan = trainingPlanSkeleton.getPlan();
        
        // Get all interval run sessions
        List<TrainingSession> intervalRunSessions = new ArrayList<>();
        for (List<TrainingSession> sessions : plan.values()) {
            for (TrainingSession session : sessions) {
                if (session != null && session.getTrainingType() == TrainingType.INTERVAL) {
                    intervalRunSessions.add(session);
                }
            }
        }

        if (this.intervalCount > 0) {
            // Distance progression: from floor((goalDistance / 400) / 4) * 400 to floor((goalDistance / 400) / 2) * 400
            double startDistance = Math.floor((goalDistance / 400.0) / 4.0) * 400.0;
            double endDistance = Math.floor((goalDistance / 400.0) / 2.0) * 400.0;
            double distanceIncrement = this.intervalCount > 1 ?
                (endDistance - startDistance) / (this.intervalCount - 1) :
                0.0;

            // Calculate pace using highIntensityMeanTime
            // Assume highIntensityMeanTime is for standardDistance (e.g., 5K)
            double referenceDistance = this.standardDistance.getMeters();
            double highIntensityPace = highIntensityMeanTime / (referenceDistance / 1000); // seconds/km

            for (int i = 0; i < this.intervalCount; i++) {
                // Calculate distance for this session
                double rawDistance = startDistance + (i * distanceIncrement);
                double distance = Math.floor(rawDistance / 400.0) * 400.0;

                // Calculate duration based on highIntensityPace
                double duration = highIntensityPace * (distance / 1000); // seconds

                // Set session attributes
                intervalRunSessions.get(i).setDistance(distance);
                intervalRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);
                intervalRunSessions.get(i).setGoalPace(Math.round(highIntensityPace * 100.0) / 100.0);
            }
        }
    }

    // Distance: 25% of goal distance
    // Duration: Low intensity pace

    private void setRecoveryRun() { 
        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();

        Map<Integer, List<TrainingSession>> plan = trainingPlanSkeleton.getPlan();
        
        // Get all recovery run sessions
        List<TrainingSession> recoveryRunSessions = new ArrayList<>();
        for (List<TrainingSession> sessions : plan.values()) {
            for (TrainingSession session : sessions) {
                if (session != null && session.getTrainingType() == TrainingType.RECOVERY_RUN) {
                    recoveryRunSessions.add(session);
                }
            }
        }

        if (this.recoveryRunCount > 0) {
            double distance = goalDistance * 0.25; // 25%

            // Calculate duration using lowIntensityMeanTime
            double referenceDistance = this.standardDistance.getMeters();
            double lowIntensityPace = lowIntensityMeanTime / (referenceDistance / 1000); 
            double duration = lowIntensityPace * (distance / 1000); 

            for (int i = 0; i < this.recoveryRunCount; i++) {
                // Set session attributes
                recoveryRunSessions.get(i).setDistance(distance);
                recoveryRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);
                recoveryRunSessions.get(i).setGoalPace(Math.round(lowIntensityPace * 100.0) / 100.0);
            }
        }
    }
}
