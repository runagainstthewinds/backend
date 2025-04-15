package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

import lombok.Getter;

/**
 * Creates a training plan based on runner history, difficulty, length, and goal distance.
 * Configures sessions for long runs, tempo runs, intervals, and recovery runs.
 */
@Getter
public class TrainingPlanCreator {

    // Constants
    private static final double LONG_RUN_START_FACTOR = 0.5;
    private static final double LONG_RUN_END_FACTOR = 1.0;
    private static final double TEMPO_RUN_DISTANCE_FACTOR = 0.4;
    private static final double TEMPO_START_DURATION_FACTOR = 1.0;
    private static final double TEMPO_END_DURATION_FACTOR = 0.9;
    private static final double INTERVAL_START_FACTOR = 0.25;
    private static final double INTERVAL_END_FACTOR = 0.5;
    private static final double RECOVERY_RUN_DISTANCE_FACTOR = 0.25;
    private static final double INTERVAL_SET_DISTANCE = 400.0;
    private static final int ROUNDING_SCALE = 100;

    // Inputs
    private final TrainingSession[] runHistory;
    private final Difficulty difficulty; // Easy, Medium, Hard
    private final int length; // weeks
    private final int goalDistance; // m 
    private final StandardDistance standardDistance = StandardDistance.FIVE_KM; 
    private final RunnerStatistics runnerStatistics;
    private final TrainingPlanSkeleton trainingPlanSkeleton;

    // Session counts
    private final Map<TrainingType, Integer> sessionCounts;
    private final Map<TrainingType, List<TrainingSession>> sessionsByType;

    public TrainingPlanCreator(TrainingSession[] runHistory, Difficulty difficulty, int length, int goalDistance) throws MissingDataException {
        validateInputs(runHistory, difficulty, length, goalDistance, standardDistance);

        this.runHistory = runHistory;
        this.difficulty = difficulty;
        this.length = length;
        this.goalDistance = goalDistance;
        this.runnerStatistics = new RunnerStatistics(this.runHistory, StandardDistance.FIVE_KM);
        this.trainingPlanSkeleton = new TrainingPlanSkeleton(this.difficulty, this.length);

        this.sessionCounts = new EnumMap<>(TrainingType.class);
        this.sessionsByType = new EnumMap<>(TrainingType.class);
        initializeSessionData();
        configureAllRuns();
    }

    /**
     * Validates constructor inputs.
     */
    private void validateInputs(TrainingSession[] runHistory, Difficulty difficulty, int length,
                               int goalDistance, StandardDistance standardDistance) {
        if (runHistory == null || runHistory.length == 0) {
            throw new MissingDataException("Run history cannot be null or empty");
        }
        for (TrainingSession session : runHistory) {
            if (session == null) {
                throw new MissingDataException("Run history contains null sessions");
            }
        }
        if (difficulty == null) {
            throw new MissingDataException("Difficulty cannot be null");
        }
        if (length <= 0) {
            throw new MissingDataException("Plan length must be positive");
        }
        if (goalDistance <= 0) {
            throw new MissingDataException("Goal distance must be positive");
        }
        if (standardDistance == null) {
            throw new MissingDataException("Standard distance cannot be null");
        }
    }

    /**
     * Initializes session counts and organizes sessions by type.
     */
    private void initializeSessionData() {
        // Initialize maps
        for (TrainingType type : TrainingType.values()) {
            sessionsByType.put(type, new ArrayList<>());
            sessionCounts.put(type, 0);
        }

        // Populate counts and sessions
        for (List<TrainingSession> weekSessions : trainingPlanSkeleton.getPlan().values()) {
            for (TrainingSession session : weekSessions) {
                if (session != null && session.getTrainingType() != null) {
                    TrainingType type = session.getTrainingType();
                    sessionCounts.compute(type, (k, v) -> v == null ? 1 : v + 1);
                    sessionsByType.get(type).add(session);
                }
            }
        }
    }

    /*
     * Creates the training sessions for all types of run.
     */
    private void configureAllRuns() {
        configureLongRun();
        configureTempoRun();
        configureIntervalRun();
        configureRecoveryRun();
    }

    /*
     * Configures long run sessions with increasing distance at low intensity.
     */
    private void configureLongRun() {
        List<TrainingSession> longRunSessions = sessionsByType.get(TrainingType.LONG_RUN);
        int count = sessionCounts.getOrDefault(TrainingType.LONG_RUN, 0);

        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();
        double startDistance = goalDistance * LONG_RUN_START_FACTOR;
        double endDistance = goalDistance * LONG_RUN_END_FACTOR;      
        double increment = count > 1 ? (endDistance - startDistance) / (count - 1) : 0.0;
    
        for (int i = 0; i < count; i++) {
            TrainingSession session = longRunSessions.get(i);
            double distance = startDistance + (i * increment);
            double duration = RiegelConverter.predictTime(this.standardDistance.getMeters(), lowIntensityMeanTime, distance);
            double pace = duration / (distance / 1000);

            session.setDistance(distance);
            session.setDuration(roundToScale(duration));
            session.setGoalPace(roundToScale(pace));
        }
    }

    /**
     * Configures tempo run sessions with fixed distance and increasing speed.
     */
    private void configureTempoRun() {
        List<TrainingSession> tempoRunSessions = sessionsByType.get(TrainingType.TEMPO);
        int count = sessionCounts.getOrDefault(TrainingType.TEMPO, 0);

        double mediumIntensityMeanTime = this.runnerStatistics.getMediumIntensityMeanTime();
        double distance = goalDistance * TEMPO_RUN_DISTANCE_FACTOR; 
        double startDurationFactor = TEMPO_START_DURATION_FACTOR; 
        double endDurationFactor = TEMPO_END_DURATION_FACTOR; 
        double durationIncrement = count > 1 ?(endDurationFactor - startDurationFactor) / (count - 1) : 0.0;

        for (int i = 0; i < count; i++) {
            tempoRunSessions.get(i).setDistance(distance);
            
            double durationFactor = startDurationFactor + (i * durationIncrement);
            double duration = mediumIntensityMeanTime * durationFactor;
            tempoRunSessions.get(i).setDuration(Math.round(duration * 100.0) / 100.0);

            double pace = duration / (distance / 1000);
            tempoRunSessions.get(i).setGoalPace(Math.round(pace * 100.0) / 100.0);
        }
    }

    /**
     * Configures interval run sessions with increasing sets at high intensity.
     */
    private void configureIntervalRun() {
        List<TrainingSession> intervalRunSessions = sessionsByType.get(TrainingType.INTERVAL);
        int count = sessionCounts.getOrDefault(TrainingType.INTERVAL, 0);

        double highIntensityMeanTime = this.runnerStatistics.getHighIntensityMeanTime();
        double startDistance = Math.floor((goalDistance * INTERVAL_START_FACTOR) / INTERVAL_SET_DISTANCE) * INTERVAL_SET_DISTANCE;
        double endDistance = Math.floor((goalDistance * INTERVAL_END_FACTOR) / INTERVAL_SET_DISTANCE) * INTERVAL_SET_DISTANCE;
        double distanceIncrement = count > 1 ? (endDistance - startDistance) / (count - 1) : 0.0;
        double setDuration = RiegelConverter.predictTime(this.standardDistance.getMeters(), highIntensityMeanTime, 400.0);

        for (int i = 0; i < count; i++) {
            TrainingSession session = intervalRunSessions.get(i);
            double rawDistance = startDistance + (i * distanceIncrement);
            double distance = Math.floor(rawDistance / INTERVAL_SET_DISTANCE) * INTERVAL_SET_DISTANCE;
            double totalDuration = setDuration * (distance / INTERVAL_SET_DISTANCE);
            double pace = totalDuration / (distance / 1000);

            session.setDistance(distance);
            session.setDuration(roundToScale(totalDuration));
            session.setGoalPace(roundToScale(pace));
        }
    }

    /**
     * Configures recovery run sessions with fixed distance and low intensity.
     */
    private void configureRecoveryRun() { 
        List<TrainingSession> recoveryRunSessions = sessionsByType.get(TrainingType.RECOVERY_RUN);
        int count = sessionCounts.getOrDefault(TrainingType.RECOVERY_RUN, 0);

        double lowIntensityMeanTime = this.runnerStatistics.getLowIntensityMeanTime();
        double distance = goalDistance * RECOVERY_RUN_DISTANCE_FACTOR; 
        double lowIntensityPace = lowIntensityMeanTime / (this.standardDistance.getMeters() / 1000); 
        double duration = lowIntensityPace * (distance / 1000); 

        for (int i = 0; i < count; i++) {
            TrainingSession session = recoveryRunSessions.get(i);
            session.setDistance(distance);
            session.setDuration(roundToScale(duration));
            session.setGoalPace(roundToScale(lowIntensityPace));
        }
    }

    /**
     * Rounds a value to two decimal places.
     */
    private double roundToScale(double value) {
        return Math.round(value * ROUNDING_SCALE) / (double) ROUNDING_SCALE;
    }
}
