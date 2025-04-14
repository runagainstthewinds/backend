package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

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
    private final int length; // Weeks
    private HashMap<Integer, List<TrainingSession>> plan = new HashMap<>();

    public TrainingPlanSkeleton(Difficulty difficulty, int length) {
        if (difficulty == null || length <= 0) throw new IllegalArgumentException("Invalid parameters");

        this.difficulty = difficulty;
        this.length = length;
        this.plan = createTrainingPlanSkeleton();
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

        int halfwayPoint = (int) Math.ceil(length / 2.0);  
        for (int week = 1; week <= length; week++) {
            List<TrainingSession> weeklySessions = new ArrayList<>();
            // Increase sessions by 1 after halfway point
            int sessionsThisWeek = (week <= halfwayPoint) ? 
                baseSessionsPerWeek : 
                baseSessionsPerWeek + 1;

            // Easy: Tempo and Long Run, add Interval after halfway
            if (difficulty == Difficulty.EASY) {
                weeklySessions.add(createSession(TrainingType.TEMPO));
                weeklySessions.add(createSession(TrainingType.LONG_RUN));
                if (week > halfwayPoint) weeklySessions.add(createSession(TrainingType.INTERVAL));
            } else {
                // Medium and Hard: Interval, Tempo, Long Run, rest Recovery
                weeklySessions.add(createSession(TrainingType.INTERVAL));
                weeklySessions.add(createSession(TrainingType.TEMPO));
                weeklySessions.add(createSession(TrainingType.LONG_RUN));

                // Fill remaining sessions with Recovery Runs
                for (int i = 3; i < sessionsThisWeek; i++) {
                    weeklySessions.add(createSession(TrainingType.RECOVERY_RUN));
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
        return session;
    }
}
