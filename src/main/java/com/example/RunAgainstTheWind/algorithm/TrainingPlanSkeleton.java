package com.example.RunAgainstTheWind.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

import lombok.Data;

/*
 * Input: Training plan difficulty (Easy, Medium, Hard), Program length (week)
 * Output: Dictionary mapping {week:sessions}
 */
@Data
public class TrainingPlanSkeleton {
    
    private String difficulty; // Easy, Medium, Hard
    private int length; // Weeks

    private HashMap<Integer, List<TrainingSession>> plan = new HashMap<>();

    public TrainingPlanSkeleton(String difficulty, int length) {
        this.difficulty = difficulty;
        this.length = length;

        this.plan = createTrainingPlanSkeleton();
    }

    public HashMap<Integer, List<TrainingSession>> createTrainingPlanSkeleton() {
        HashMap<Integer, List<TrainingSession>> plan = new HashMap<>();

        int baseSessionsPerWeek;
        switch (difficulty.toLowerCase()) {
            case "easy":
                baseSessionsPerWeek = 2;
                break;
            case "medium":
                baseSessionsPerWeek = 4;
                break;
            case "hard":
                baseSessionsPerWeek = 6;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }

        int halfwayPoint = (int) Math.ceil(length / 2.0);
        for (int week = 1; week <= length; week++) {
            List<TrainingSession> weeklySessions = new ArrayList<>();
            // Increase sessions by 1 after halfway point
            int sessionsThisWeek = (week <= halfwayPoint) ? 
                baseSessionsPerWeek : 
                baseSessionsPerWeek + 1;

            // Easy: Tempo and Long Run, add Interval after halfway
            if (difficulty.equalsIgnoreCase("easy")) {
                TrainingSession tempo = new TrainingSession();
                tempo.setTrainingType(TrainingType.TEMPO);
                weeklySessions.add(tempo);

                TrainingSession longRun = new TrainingSession();
                longRun.setTrainingType(TrainingType.LONG_RUN);
                weeklySessions.add(longRun);

                if (week > halfwayPoint) {
                    TrainingSession interval = new TrainingSession();
                    interval.setTrainingType(TrainingType.INTERVAL);
                    weeklySessions.add(interval);
                }
            } else {
                // Medium and Hard: Interval, Tempo, Long Run, rest Recovery
                TrainingSession interval = new TrainingSession();
                interval.setTrainingType(TrainingType.INTERVAL);
                weeklySessions.add(interval);

                TrainingSession tempo = new TrainingSession();
                tempo.setTrainingType(TrainingType.TEMPO);
                weeklySessions.add(tempo);

                TrainingSession longRun = new TrainingSession();
                longRun.setTrainingType(TrainingType.LONG_RUN);
                weeklySessions.add(longRun);

                // Fill remaining sessions with Recovery Runs
                for (int i = 3; i < sessionsThisWeek; i++) {
                    TrainingSession recovery = new TrainingSession();
                    recovery.setTrainingType(TrainingType.RECOVERY_RUN);
                    weeklySessions.add(recovery);
                }
            }

            plan.put(week, weeklySessions);
        }

        return plan;
    }
}
