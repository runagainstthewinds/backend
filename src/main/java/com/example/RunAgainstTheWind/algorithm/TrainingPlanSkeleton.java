package com.example.RunAgainstTheWind.algorithm;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;

import lombok.Data;

/**
 * A class representing a skeleton for a training plan, defining weekly sessions based on difficulty and duration.
 */
@Data
public class TrainingPlanSkeleton {

    private static final int EASY_SESSIONS_PER_WEEK = 2;
    private static final int MEDIUM_SESSIONS_PER_WEEK = 4;
    private static final int HARD_SESSIONS_PER_WEEK = 6;
    
    private final Difficulty difficulty; // Easy, Medium, Hard
    private final Double length; // Weeks
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final TrainingPlan trainingPlan;
    private HashMap<Integer, List<TrainingSession>> plan = new HashMap<>();

    /**
     * Constructor that takes difficulty and dates, calculating length from full weeks.
     */
    public TrainingPlanSkeleton(Difficulty difficulty, LocalDate startDate, LocalDate endDate, TrainingPlan trainingPlan) {
        if (difficulty == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.trainingPlan = trainingPlan;
        this.length = calculateFullWeeks(startDate, endDate);
        if (this.length <= 0) {
            throw new IllegalArgumentException("No full weeks found between start and end dates");
        }
        this.plan = createTrainingPlanSkeleton();
    }

    /**
     * Calculates the number of full weeks between start and end dates.
     * A full week is defined as starting on Monday and ending on Sunday.
     * 
     * @param startDate The start date of the training plan
     * @param endDate The end date of the training plan
     * @return The number of full weeks as a Double
     */
    private static Double calculateFullWeeks(LocalDate startDate, LocalDate endDate) {
        // Adjust start date to next Monday if not already Monday
        LocalDate adjustedStartDate = startDate;
        if (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            adjustedStartDate = startDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        
        // Adjust end date to previous Sunday if not already Sunday
        LocalDate adjustedEndDate = endDate;
        if (endDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            adjustedEndDate = endDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        }
        
        // Calculate weeks between adjusted dates
        long days = ChronoUnit.DAYS.between(adjustedStartDate, adjustedEndDate) + 1;
        return Double.valueOf(Math.floor(days / 7.0));
    }

    /**
     * Creates a training plan skeleton with weekly sessions based on difficulty and duration.
     *
     * @return A HashMap mapping each week to a list of training sessions
     */
    public HashMap<Integer, List<TrainingSession>> createTrainingPlanSkeleton() {
        HashMap<Integer, List<TrainingSession>> newPlan = new HashMap<>();

        int baseSessionsPerWeek = switch (difficulty) {
            case EASY -> EASY_SESSIONS_PER_WEEK;
            case MEDIUM -> MEDIUM_SESSIONS_PER_WEEK;
            case HARD -> HARD_SESSIONS_PER_WEEK;
            default -> throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        };

        // Get the first Monday of the training plan
        LocalDate weekStart = startDate;
        if (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            weekStart = startDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }

        int halfwayPoint = (int) Math.ceil(length / 2.0);  
        for (int week = 1; week <= length; week++) {
            List<TrainingSession> weeklySessions = new ArrayList<>();
            // Increase sessions by 1 after halfway point
            int sessionsThisWeek = (week <= halfwayPoint) ? 
                baseSessionsPerWeek : 
                baseSessionsPerWeek + 1;

            // Calculate dates for this week
            LocalDate monday = weekStart.plusWeeks(week - 1);
            LocalDate tuesday = monday.plusDays(1);
            LocalDate wednesday = monday.plusDays(2);
            LocalDate thursday = monday.plusDays(3);
            LocalDate friday = monday.plusDays(4);
            LocalDate saturday = monday.plusDays(5);

            // Easy: Tempo and Long Run, add Interval after halfway
            if (difficulty == Difficulty.EASY) {
                // Monday: Tempo Run
                TrainingSession tempoSession = createSession(TrainingType.TEMPO);
                tempoSession.setDate(monday);
                weeklySessions.add(tempoSession);

                // Saturday: Long Run
                TrainingSession longRunSession = createSession(TrainingType.LONG_RUN);
                longRunSession.setDate(saturday);
                weeklySessions.add(longRunSession);

                // Wednesday: Interval (after halfway point)
                if (week > halfwayPoint) {
                    TrainingSession intervalSession = createSession(TrainingType.INTERVAL);
                    intervalSession.setDate(wednesday);
                    weeklySessions.add(intervalSession);
                }
            } else {
                // Medium and Hard: Interval, Tempo, Long Run, rest Recovery
                // Monday: Tempo Run
                TrainingSession tempoSession = createSession(TrainingType.TEMPO);
                tempoSession.setDate(monday);
                weeklySessions.add(tempoSession);

                // Wednesday: Interval
                TrainingSession intervalSession = createSession(TrainingType.INTERVAL);
                intervalSession.setDate(wednesday);
                weeklySessions.add(intervalSession);

                // Saturday: Long Run
                TrainingSession longRunSession = createSession(TrainingType.LONG_RUN);
                longRunSession.setDate(saturday);
                weeklySessions.add(longRunSession);

                // Fill remaining sessions with Recovery Runs
                // For Medium: 1 recovery run on Thursday
                // For Hard: 1 recovery run on Tuesday, 1 on Thursday, and 1 on Friday
                if (difficulty == Difficulty.MEDIUM) {
                    TrainingSession recoverySession = createSession(TrainingType.RECOVERY_RUN);
                    recoverySession.setDate(thursday);
                    weeklySessions.add(recoverySession);
                } else if (difficulty == Difficulty.HARD) {
                    // Tuesday recovery
                    TrainingSession recovery1 = createSession(TrainingType.RECOVERY_RUN);
                    recovery1.setDate(tuesday);
                    weeklySessions.add(recovery1);

                    // Thursday recovery
                    TrainingSession recovery2 = createSession(TrainingType.RECOVERY_RUN);
                    recovery2.setDate(thursday);
                    weeklySessions.add(recovery2);

                    // Friday recovery (if needed)
                    if (sessionsThisWeek > 6) {
                        TrainingSession recovery3 = createSession(TrainingType.RECOVERY_RUN);
                        recovery3.setDate(friday);
                        weeklySessions.add(recovery3);
                    }
                }
            }

            newPlan.put(week, weeklySessions);
        }

        return newPlan;
    }

    /**
     * Creates a training session with the specified type.
     *
     * @param type The type of training session
     * @return A new TrainingSession instance
     */
    private TrainingSession createSession(TrainingType type) {
        TrainingSession session = new TrainingSession();
        session.setTrainingType(type);
        session.setTrainingPlan(trainingPlan);
        return session;
    }
}
