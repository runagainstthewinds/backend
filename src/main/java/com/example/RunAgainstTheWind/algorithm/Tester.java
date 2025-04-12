package com.example.RunAgainstTheWind.algorithm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;

public class Tester {
    public static void main(String[] args) {
        // Sample TrainingSession data
        TrainingSession[] trainingSessions = {
            // 4:00 pace (10,000m in 40s)
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 10000.0, 40.0, 0, TrainingType.UNSPECIFIED
            ),
            // 5:00 pace (3,000m in 15s)
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 3000.0, 15.0, 0, TrainingType.UNSPECIFIED
            ),
            // 6:00 pace (1,000m in 6s)
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 1000.0, 6.0, 0, TrainingType.UNSPECIFIED
            )
        };

        // Initialize TrainingPlanCreator
        try {
            TrainingPlanCreator planCreator = new TrainingPlanCreator(
                trainingSessions, // runHistory
                "hard",          // difficulty
                6,               // length (weeks)
                10000             // distance (meters, e.g., 5K)
            );

            // Access RunnerStatistics via TrainingPlanCreator
            RunnerStatistics runnerStatistics = planCreator.getRunnerStatistics();

            // Print RunnerStatistics information
            System.out.println("Sessions: ");
            System.out.println("High Intensity Sessions: " + runnerStatistics.getHighIntensitySessions());
            System.out.println("Medium Intensity Sessions: " + runnerStatistics.getMediumIntensitySessions());
            System.out.println("Low Intensity Sessions: " + runnerStatistics.getLowIntensitySessions());
            //System.out.println("Statistics: ");
            //System.out.println("Mean time: " + runnerStatistics.getMeanTime());
            //System.out.println("Standard deviation: " + runnerStatistics.getStandardDeviation());
            //System.out.println("Fast Cutoff: " + runnerStatistics.getFastCutoff());
            //System.out.println("Slow Cutoff: " + runnerStatistics.getSlowCutoff());

            // Print training type counts (set by TrainingPlanCreator)
            System.out.println("Training Type Counts: ");
            System.out.println("Tempo sessions: " + planCreator.getTempoCount());
            System.out.println("Long Run sessions: " + planCreator.getLongRunCount());
            System.out.println("Interval sessions: " + planCreator.getIntervalCount());
            System.out.println("Recovery Run sessions: " + planCreator.getRecoveryRunCount());
            System.out.println();

            // Access TrainingPlanSkeleton via TrainingPlanCreator
            TrainingPlanSkeleton trainingPlanSkeleton = planCreator.getTrainingPlanSkeleton();

            // Print TrainingPlanSkeleton with custom formatting
            System.out.println("Training Plan Skeleton: ");
            printTrainingPlan(trainingPlanSkeleton.getPlan());

        } catch (MissingDataException e) {
            System.out.println("Error: " + e.getMessage());
            return; // Exit if there's an error in initialization
        }
    }

    // Custom method to print the training plan
    private static void printTrainingPlan(Map<Integer, List<TrainingSession>> plan) {
        for (Map.Entry<Integer, List<TrainingSession>> entry : plan.entrySet()) {
            int week = entry.getKey();
            List<TrainingSession> sessions = entry.getValue();
            System.out.println("Week " + week + ":");
            for (int i = 0; i < sessions.size(); i++) {
                TrainingSession session = sessions.get(i);
                String distance = session.getDistance() != null ? 
                    String.format("%.0fm", session.getDistance()) : 
                    "Not set";
                System.out.println("  Session " + (i + 1) + ": " + 
                    session.getTrainingType() + " (Distance: " + distance + ", Time: " + session.getDuration() + ", Pace: " + session.getGoalPace() + ")");
            }
            System.out.println(); // Blank line between weeks
        }
    }
}