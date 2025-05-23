package com.example.RunAgainstTheWind.algorithm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;

import lombok.Getter;

/**
 * Creates a training plan based on runner history, difficulty, dates, and goal distance.
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
    private static final double INTERVAL_DURATION_FACTOR = 1.4;
    private static final double RECOVERY_RUN_DISTANCE_FACTOR = 0.25;
    private static final double INTERVAL_SET_DISTANCE = 0.5;
    private static final int ROUNDING_SCALE = 100;

    // Inputs
    private final List<TrainingSessionDTO> runHistory;
    private final Difficulty difficulty; // Easy, Medium, Hard
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Double goalDistance; // in meters
    private final StandardDistance standardDistance = StandardDistance.FIVE_KM; 
    private final RunnerStatistics runnerStatistics;
    private final TrainingPlanSkeleton trainingPlanSkeleton;
    private final TrainingPlan trainingPlan;

    // Session counts
    private final Map<TrainingType, Integer> sessionCounts;
    private final Map<TrainingType, List<TrainingSession>> sessionsByType;

    /**
     * Creates a new training plan creator.
     * @param runHistory List of previous training sessions
     * @param difficulty Difficulty level of the plan
     * @param startDate Start date of the plan
     * @param endDate End date of the plan
     * @param goalDistanceKm Goal distance in kilometers
     * @param trainingPlan The training plan to associate sessions with
     * @throws MissingDataException if required data is missing
     */
    public TrainingPlanCreator(List<TrainingSessionDTO> runHistory, Difficulty difficulty, 
                             LocalDate startDate, LocalDate endDate, Double goalDistanceKm,
                             TrainingPlan trainingPlan) throws MissingDataException {
        
        validateInputs(runHistory, difficulty, startDate, endDate, goalDistanceKm, standardDistance);

        this.runHistory = runHistory;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalDistance = goalDistanceKm;
        this.trainingPlan = trainingPlan;
        this.runnerStatistics = new RunnerStatistics(this.runHistory, StandardDistance.FIVE_KM);
        this.trainingPlanSkeleton = new TrainingPlanSkeleton(this.difficulty, this.startDate, this.endDate, this.trainingPlan);

        this.sessionCounts = new EnumMap<>(TrainingType.class);
        this.sessionsByType = new EnumMap<>(TrainingType.class);
        initializeSessionData();
        configureAllRuns();
    }

    /**
     * Validates constructor inputs.
     */
    private void validateInputs(List<TrainingSessionDTO> runHistory, Difficulty difficulty, 
                              LocalDate startDate, LocalDate endDate, Double goalDistance, 
                              StandardDistance standardDistance) {
        if (runHistory == null || runHistory.isEmpty()) {
            throw new MissingDataException("Run history cannot be null or empty");
        }
        for (TrainingSessionDTO session : runHistory) {
            if (session == null) {
                throw new MissingDataException("Run history contains null sessions");
            }
        }
        if (difficulty == null) {
            throw new MissingDataException("Difficulty cannot be null");
        }
        if (startDate == null) {
            throw new MissingDataException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new MissingDataException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new MissingDataException("End date must be after start date");
        }
        if (goalDistance <= 0.0) {
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
            double duration = RiegelConverter.predictTime(this.standardDistance.getKilometers(), lowIntensityMeanTime, distance);
            double pace = duration / (distance);

            session.setDistance(distance);
            session.setDuration(roundToScale(duration));
            session.setPace(roundToScale(pace));
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
            TrainingSession session = tempoRunSessions.get(i);
            session.setDistance(distance);
            
            double durationFactor = startDurationFactor + (i * durationIncrement);
            double duration = mediumIntensityMeanTime * durationFactor;
            session.setDuration(Math.round(duration * 100.0) / 100.0);

            double pace = duration / (distance);
            session.setPace(Math.round(pace * 100.0) / 100.0);
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
        double setDuration = RiegelConverter.predictTime(this.standardDistance.getKilometers(), highIntensityMeanTime, 0.4);
        setDuration = setDuration * INTERVAL_DURATION_FACTOR;

        for (int i = 0; i < count; i++) {
            TrainingSession session = intervalRunSessions.get(i);
            double rawDistance = startDistance + (i * distanceIncrement);
            double distance = Math.floor(rawDistance / 0.4) * 0.4;
            double totalDuration = setDuration * (distance / INTERVAL_SET_DISTANCE);
            double pace = totalDuration / (distance);

            session.setDistance(distance);
            session.setDuration(roundToScale(totalDuration));
            session.setPace(roundToScale(pace));
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
        double lowIntensityPace = lowIntensityMeanTime / (this.standardDistance.getKilometers()); 
        double duration = lowIntensityPace * (distance); 

        for (int i = 0; i < count; i++) {
            TrainingSession session = recoveryRunSessions.get(i);
            session.setDistance(distance);
            session.setDuration(roundToScale(duration));
            session.setPace(roundToScale(lowIntensityPace));
        }
    }

    /**
     * Rounds a value to two decimal places.
     */
    private double roundToScale(double value) {
        return Math.round(value * ROUNDING_SCALE) / (double) ROUNDING_SCALE;
    }
}
