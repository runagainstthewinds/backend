package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

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

    private int longRunCount = 0;
    private int tempoCount = 0;
    private int intervalCount = 0;
    private int recoveryRunCount = 0;

    public TrainingPlanCreator(TrainingSession[] runHistory, String difficulty, int length, int goalDistance) throws MissingDataException {
        if (runHistory == null || runHistory.length == 0 || difficulty == null || length <= 0 || goalDistance <= 0) {
            throw new MissingDataException("Missing Data to create training plan.");
        }

        this.runHistory = runHistory;
        this.difficulty = difficulty;
        this.length = length;
        this.goalDistance = goalDistance;
        this.runnerStatistics = new RunnerStatistics(this.runHistory, StandardDistance.FIVE_KM, 1.0, 1.0);
        this.trainingPlanSkeleton = new TrainingPlanSkeleton(this.difficulty, this.length);

        countTrainingTypes();
        setAllRuns();
    }

    /*
     * Creates the training sessions for all types of run.
     */
    private void setAllRuns() {
        if (this.longRunCount != 0) setLongRun();
        if (this.tempoCount != 0) setTempoRun();
        if (this.intervalCount != 0) setIntervalRun();
        if (this.recoveryRunCount != 0) setRecoveryRun();
    }

    /*
     * Long run intensity is always low, but we increase the distance each week
     * Distance: Starts at 50% of goal distance. Increases to 100%
     * Duration: Low intensity pace
     */
    private void setLongRun() {
        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();
        List<TrainingSession> longRunSessions = getSessionsByType(TrainingType.LONG_RUN);

        // Calculate distance increment
        double startDistance = goalDistance * 0.5;
        double endDistance = goalDistance;      
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

    /*
     * Tempo run distance is the same, but we attempt to increase speed each week
     * Distance: 40% of goal distance
     * Duration: Starts at medium instensity pace and decreases each week
     */
    private void setTempoRun() {
        double mediumIntensityMeanTime = this.runnerStatistics.getMediumIntensityMeanTime();
        List<TrainingSession> tempoRunSessions = getSessionsByType(TrainingType.TEMPO);

        // Calculate pace decrease
        double distance = goalDistance * 0.4; 
        double startDurationFactor = 1.0; 
        double endDurationFactor = 0.90; 
        double durationIncrement = this.tempoCount > 1 ?
            (endDurationFactor - startDurationFactor) / (this.tempoCount - 1) :
            0.0;

        for (int i = 0; i < this.tempoCount; i++) {
            tempoRunSessions.get(i).setDistance(distance);
            
            double durationFactor = startDurationFactor + (i * durationIncrement);
            double duration = mediumIntensityMeanTime * durationFactor;
            tempoRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);

            double pace = duration / (distance / 1000);
            tempoRunSessions.get(i).setGoalPace(Math.round(pace * 100.0) / 100.0);
        }
    }

    /*
     * We do intervals of 400 meters. Each week, we increase the numbers of set rather than the distance or speed
     * Distance is the total distance of the intervals. It does not include the recoeery in-between. 
     * Distance: Start of with X sets of 400m where total is around 25% of goal distance. We finish where total is around 50%.
     * Duration: High intensity pace
     */
    private void setIntervalRun() {
        double highIntensityMeanTime = this.runnerStatistics.getHighIntensityMeanTime();
        List<TrainingSession> intervalRunSessions = getSessionsByType(TrainingType.INTERVAL);

        // Calculate sets of 400m intervals
        double startDistance = Math.floor((goalDistance / 400.0) / 4.0) * 400.0;
        double endDistance = Math.floor((goalDistance / 400.0) / 2.0) * 400.0;
        double distanceIncrement = this.intervalCount > 1 ?
            (endDistance - startDistance) / (this.intervalCount - 1) :
            0.0;

        // Calculate duration
        double setDuration = RiegelConverter.predictTime(this.standardDistance.getMeters(), highIntensityMeanTime, 400.0);

        for (int i = 0; i < this.intervalCount; i++) {
            double rawDistance = startDistance + (i * distanceIncrement);
            double distance = Math.floor(rawDistance / 400.0) * 400.0;
            intervalRunSessions.get(i).setDistance(distance);

            double totalDuration = setDuration * (distance / 400.0);
            intervalRunSessions.get(i).setDuration(Math.round(totalDuration * 100.0) / 100.0);

            double pace = totalDuration / (distance / 1000);
            intervalRunSessions.get(i).setGoalPace(Math.round(pace * 100.0) / 100.0);
        }
    }

    /*
     * All recovery runs are the same.
     * Distance: 25% of goal distance
     * Duration: Low intensity pace
     */
    private void setRecoveryRun() { 
        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();
        List<TrainingSession> recoveryRunSessions = getSessionsByType(TrainingType.RECOVERY_RUN);

        double distance = goalDistance * 0.25; 
        double lowIntensityPace = lowIntensityMeanTime / (this.standardDistance.getMeters() / 1000); 
        double duration = lowIntensityPace * (distance / 1000); 

        for (int i = 0; i < this.recoveryRunCount; i++) {
            recoveryRunSessions.get(i).setDistance(distance);
            recoveryRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);
            recoveryRunSessions.get(i).setGoalPace(Math.round(lowIntensityPace * 100.0) / 100.0);
        }
    }

    /* 
     * Helper method to count the number of each training type in the run history.
     */
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

    /*
     * Helper method to get sessions of a specific type from the training plan skeleton.
     */
    private List<TrainingSession> getSessionsByType(TrainingType runType) {
        List<TrainingSession> sessions = new ArrayList<>();
        for (List<TrainingSession> weekSessions : this.trainingPlanSkeleton.getPlan().values()) {
            for (TrainingSession session : weekSessions) {
                if (session != null && session.getTrainingType() == runType) {
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

}
