package com.example.RunAgainstTheWind.exceptions.trainingPlan;

public class ActiveTrainingPlanExistsException extends RuntimeException {
    public ActiveTrainingPlanExistsException(String message) {
        super(message);
    }
}
