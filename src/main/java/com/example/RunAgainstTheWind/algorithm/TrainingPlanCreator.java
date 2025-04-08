package com.example.RunAgainstTheWind.algorithm;

import java.util.Date;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.StandardDistance;
import com.example.RunAgainstTheWind.enumeration.TrainingType;

public class TrainingPlanCreator {
    public static void main(String[] args) {
        TrainingSession[] trainingSessions = {
            // NOTE: This is where splitting into upcoming and completed sessions would make it less redundant. For the algorithm,
            // we only really need the fields of achieved distance, and achieved duration. At the same time, this is the only place where we need
            // to GET /trainingSessions for the foreseeable future, so it might not be necessary.

            // Date, distance(m), duration(s), goalPace, isCompleted, achievedPace, achievedDistance, achievedDuration, effort
            new TrainingSession(
                // 4:00 pace
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 10000.0, 40.0, 0, TrainingType.NONE
            ),
            new TrainingSession(
                // 5:00 pace
                new Date(), 0.0, 0.0,  0.0, true, 0.0, 3000.0, 15.0, 0, TrainingType.NONE
            ),
            new TrainingSession(
                // 6:00 pace
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 1000.0, 6.0, 0, TrainingType.NONE
            )
        };

        RunnerStatistics runnerStatistics = new RunnerStatistics(trainingSessions, StandardDistance.FIVE_KM, 1.0, 1.0);

        System.out.println("Sessions: ");
        System.out.println("High Intensity Sessions: " + runnerStatistics.getHighIntensitySessions());
        System.out.println("Average Intensity Sessions: " + runnerStatistics.getAverageIntensitySessions());
        System.out.println("Low Intensity Sessions: " + runnerStatistics.getLowIntensitySessions());
        System.out.println("Statistics: ");
        System.out.println("Mean time: " + runnerStatistics.getMeanTime());
        System.out.println("Standard deviation: " + runnerStatistics.getStandardDeviation());
        System.out.println("Fast Cutoff: " + runnerStatistics.getFastCutoff());
        System.out.println("Slow Cutoff: " + runnerStatistics.getSlowCutoff());

        // TRAINING PLAN SKELETON
        TrainingPlanSkeleton trainingPlanSkeleton = new TrainingPlanSkeleton("easy", 4);
        System.out.println("Training Plan Skeleton: ");
        System.out.println(trainingPlanSkeleton.createTrainingPlanSkeleton());  
    }
}
