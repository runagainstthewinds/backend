package com.example.RunAgainstTheWind.algorithm;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.dto.trainingSession.TrainingSessionDTO;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import com.example.RunAgainstTheWind.exceptions.MissingDataException;
import com.example.RunAgainstTheWind.domain.trainingPlan.model.TrainingPlan;

public class Tester {
    public static void main(String[] args) {
        // Sample TrainingSession data
        List<TrainingSessionDTO> trainingSessions = List.of(
            // 4:00 pace (10,000m in 40 minutes)
            new TrainingSessionDTO(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 10.0, 40.0, 4.0, 0, "", null, null, null
            ),
            // 5:00 pace (3,000m in 15 minutes)
            new TrainingSessionDTO(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 3.0, 17.5, 3.5, 0, "", null, null, null
            ),
            // 6:00 pace (1,000m in 6 minutes)
            new TrainingSessionDTO(
                TrainingType.UNSPECIFIED, LocalDate.now(), 0.0, 0.0, 0.0, true, 1.0, 3.0, 3.0, 0, "", null, null, null
            )
        );

        // Set up dates for a 6-week training plan
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(6);

        // Create a dummy training plan for testing
        TrainingPlan dummyTrainingPlan = new TrainingPlan(
            "Test Plan",
            startDate,
            endDate,
            10.0,
            Difficulty.HARD,
            false,
            null  // No user for testing
        );

        // Initialize TrainingPlanCreator
        try {
            TrainingPlanCreator planCreator = new TrainingPlanCreator(
                trainingSessions,         // runHistory
                Difficulty.HARD,          // difficulty
                startDate,               // startDate
                endDate,                 // endDate
                dummyTrainingPlan.getGoalDistance(),                // distance (kilometers, e.g., 5K)
                dummyTrainingPlan        // trainingPlan
            );

            // Access RunnerStatistics via TrainingPlanCreator
            RunnerStatistics runnerStatistics = planCreator.getRunnerStatistics();

            // Print RunnerStatistics information
            System.out.println("Sessions: ");
            System.out.println("High Intensity Sessions: " + runnerStatistics.getHighIntensitySessions());
            System.out.println("Medium Intensity Sessions: " + runnerStatistics.getMediumIntensitySessions());
            System.out.println("Low Intensity Sessions: " + runnerStatistics.getLowIntensitySessions());

            // Print training type counts (set by TrainingPlanCreator)
            for (TrainingType type : TrainingType.values()) {
                int count = planCreator.getSessionCounts().getOrDefault(type, 0);
                System.out.println(type + " sessions: " + count);
            }
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
                    String.format("%.2fkm", session.getDistance()) : 
                    "Not set";
                String date = session.getDate() != null ?
                    session.getDate().toString() :
                    "No date set";
                System.out.println("  Session " + (i + 1) + ": " + 
                    session.getTrainingType() + 
                    " (Date: " + date + 
                    ", Distance: " + distance + 
                    ", Time: " + session.getDuration() + 
                    ", Pace: " + session.getPace() + ")");
            }
            System.out.println(); // Blank line between weeks
        }
    }
}