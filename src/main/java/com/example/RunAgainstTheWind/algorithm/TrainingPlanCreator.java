package com.example.RunAgainstTheWind.algorithm;

import java.util.Date;

import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

public class TrainingPlanCreator {
    public static void main(String[] args) {
        TrainingSession[] trainingSessions = {
            // NOTE: This is where splitting into upcoming and completed sessions would make it less redundant. For the algorithm,
            // we only really need the fields of achieved distance, and achieved duration. At the same time, this is the only place where we need
            // to GET /trainingSessions for the foreseeable future, so it might not be necessary.

            // Date, distance(m), duration(s), goalPace, isCompleted, achievedPace, achievedDistance, achievedDuration, effort
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 10000.0, 40.5, 0
            ),
            new TrainingSession(
                new Date(), 0.0, 0.0,  0.0, true, 0.0, 3000.0, 15.0, 0
            ),
            new TrainingSession(
                new Date(), 0.0, 0.0, 0.0, true, 0.0, 1000.0, 6.0, 0
            )
        };

        // Convert all sessions to predicted 5K times
        double[] predicted5KTimes = RiegelConverter.convertAllTo5K(trainingSessions);

        // Result
        System.out.println("Predicted 5K times:");
        for (int i = 0; i < trainingSessions.length; i++) {
            System.out.println("Training Session " + i + ": " + trainingSessions[i].getAchievedDistance() + "m in " + trainingSessions[i].getAchievedDuration() + 
                              "= 5K in " + (predicted5KTimes[i]));
        }
    }
}
