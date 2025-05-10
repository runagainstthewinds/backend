package com.example.RunAgainstTheWind.algorithmTesting;

import com.example.RunAgainstTheWind.algorithm.TrainingPlanSkeleton;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;
import com.example.RunAgainstTheWind.enumeration.Difficulty;
import com.example.RunAgainstTheWind.enumeration.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingPlanSkeletonTest {

    private TrainingPlanSkeleton plan;

    @BeforeEach
    void setUp() {
        // Initialize with a default plan for reuse if needed
        plan = new TrainingPlanSkeleton(Difficulty.EASY, 6.0);
    }

    @Test
    void testEasyDifficultyFirstHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.EASY, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // First half (weeks 1-3) should have 2 sessions: Tempo, Long Run
        for (int week = 1; week <= 3; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(2, sessions.size(), "Week " + week + " should have 2 sessions");

            // Check session types
            assertTrue(sessions.stream().anyMatch(s -> s.getTrainingType() == TrainingType.TEMPO),
                    "Week " + week + " should have a TEMPO session");
            assertTrue(sessions.stream().anyMatch(s -> s.getTrainingType() == TrainingType.LONG_RUN),
                    "Week " + week + " should have a LONG_RUN session");
        }
    }

    @Test
    void testEasyDifficultySecondHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.EASY, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // Second half (weeks 4-6) should have 3 sessions: Tempo, Long Run, Interval
        for (int week = 4; week <= 6; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(3, sessions.size(), "Week " + week + " should have 3 sessions");

            // Check session types
            assertTrue(sessions.stream().anyMatch(s -> s.getTrainingType() == TrainingType.TEMPO),
                    "Week " + week + " should have a TEMPO session");
            assertTrue(sessions.stream().anyMatch(s -> s.getTrainingType() == TrainingType.LONG_RUN),
                    "Week " + week + " should have a LONG_RUN session");
            assertTrue(sessions.stream().anyMatch(s -> s.getTrainingType() == TrainingType.INTERVAL),
                    "Week " + week + " should have an INTERVAL session");
        }
    }

    @Test
    void testMediumDifficultyFirstHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.MEDIUM, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // First half (weeks 1-3) should have 4 sessions: Interval, Tempo, Long Run, Recovery
        for (int week = 1; week <= 3; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(4, sessions.size(), "Week " + week + " should have 4 sessions");

            // Check session types
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.INTERVAL).count(),
                    "Week " + week + " should have exactly 1 INTERVAL session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.TEMPO).count(),
                    "Week " + week + " should have exactly 1 TEMPO session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.LONG_RUN).count(),
                    "Week " + week + " should have exactly 1 LONG_RUN session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.RECOVERY_RUN).count(),
                    "Week " + week + " should have exactly 1 RECOVERY_RUN session");
        }
    }

    @Test
    void testMediumDifficultySecondHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.MEDIUM, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // Second half (weeks 4-6) should have 5 sessions: Interval, Tempo, Long Run, Recovery, Recovery
        for (int week = 4; week <= 6; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(5, sessions.size(), "Week " + week + " should have 5 sessions");

            // Check session types
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.INTERVAL).count(),
                    "Week " + week + " should have exactly 1 INTERVAL session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.TEMPO).count(),
                    "Week " + week + " should have exactly 1 TEMPO session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.LONG_RUN).count(),
                    "Week " + week + " should have exactly 1 LONG_RUN session");
            assertEquals(2, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.RECOVERY_RUN).count(),
                    "Week " + week + " should have exactly 2 RECOVERY_RUN sessions");
        }
    }

    @Test
    void testHardDifficultyFirstHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.HARD, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // First half (weeks 1-3) should have 6 sessions: Interval, Tempo, Long Run, Recovery, Recovery, Recovery
        for (int week = 1; week <= 3; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(6, sessions.size(), "Week " + week + " should have 6 sessions");

            // Check session types
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.INTERVAL).count(),
                    "Week " + week + " should have exactly 1 INTERVAL session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.TEMPO).count(),
                    "Week " + week + " should have exactly 1 TEMPO session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.LONG_RUN).count(),
                    "Week " + week + " should have exactly 1 LONG_RUN session");
            assertEquals(3, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.RECOVERY_RUN).count(),
                    "Week " + week + " should have exactly 3 RECOVERY_RUN sessions");
        }
    }

    @Test
    void testHardDifficultySecondHalf() {
        plan = new TrainingPlanSkeleton(Difficulty.HARD, 6.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // Second half (weeks 4-6) should have 7 sessions: Interval, Tempo, Long Run, Recovery, Recovery, Recovery, Recovery
        for (int week = 4; week <= 6; week++) {
            List<TrainingSession> sessions = trainingPlan.get(week);
            assertNotNull(sessions, "Week " + week + " sessions should not be null");
            assertEquals(7, sessions.size(), "Week " + week + " should have 7 sessions");

            // Check session types
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.INTERVAL).count(),
                    "Week " + week + " should have exactly 1 INTERVAL session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.TEMPO).count(),
                    "Week " + week + " should have exactly 1 TEMPO session");
            assertEquals(1, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.LONG_RUN).count(),
                    "Week " + week + " should have exactly 1 LONG_RUN session");
            assertEquals(4, sessions.stream().filter(s -> s.getTrainingType() == TrainingType.RECOVERY_RUN).count(),
                    "Week " + week + " should have exactly 4 RECOVERY_RUN sessions");
        }
    }

    @Test
    void testPlanLength() {
        plan = new TrainingPlanSkeleton(Difficulty.EASY, 5.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();
        assertEquals(5, trainingPlan.size(), "Plan should have 5 weeks");

        // Verify weeks are numbered 1 to 5
        for (int week = 1; week <= 5; week++) {
            assertTrue(trainingPlan.containsKey(week), "Plan should contain week " + week);
        }
    }

    @Test
    void testOddLengthHalfwayPoint() {
        plan = new TrainingPlanSkeleton(Difficulty.EASY, 5.0);
        HashMap<Integer, List<TrainingSession>> trainingPlan = plan.createTrainingPlanSkeleton();

        // Halfway point for length 5 is week 3 (ceiling of 5/2 = 3)
        assertEquals(2, trainingPlan.get(1).size(), "Week 1 should have 2 sessions");
        assertEquals(2, trainingPlan.get(2).size(), "Week 2 should have 2 sessions");
        assertEquals(2, trainingPlan.get(3).size(), "Week 3 should have 2 sessions");
        assertEquals(3, trainingPlan.get(4).size(), "Week 4 should have 3 sessions");
        assertEquals(3, trainingPlan.get(5).size(), "Week 5 should have 3 sessions");
    }
}