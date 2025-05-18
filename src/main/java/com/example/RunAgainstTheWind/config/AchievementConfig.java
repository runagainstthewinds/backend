package com.example.RunAgainstTheWind.config;

import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
public class AchievementConfig {
    
    @Getter
    @RequiredArgsConstructor
    public enum Achievement {
        FIRST_RUN(1, "First Run", "Completed your first run"),
        EARLY_BIRD(2, "Early Bird", "Completed a run before 6 AM"),
        RAIN_RUNNER(3, "Rain Runner", "Completed a run in the rain"),
        MARATHON_FINISHER(4, "Marathon Finisher", "Completed a full marathon"),
        STREAK_MASTER(5, "Streak Master", "Run for 7 consecutive days"),
        TRAIL_EXPLORER(6, "Trail Explorer", "Completed a trail run"),
        SPEED_DEMON(7, "Speed Demon", "Ran 5K under 20 minutes"),
        GLOBE_TROTTER(8, "Globe Trotter", "Run in 5 different cities"),
        CONSISTENCY_KING(9, "Consistency King", "Run 20 times in a month");

        private final int id;
        private final String name;
        private final String description;

        public static Achievement fromId(int id) {
            for (Achievement achievement : values()) {
                if (achievement.getId() == id) {
                    return achievement;
                }
            }
            throw new IllegalArgumentException("No achievement found with id: " + id);
        }

        public static Achievement fromName(String name) {
            for (Achievement achievement : values()) {
                if (achievement.getName().equals(name)) {
                    return achievement;
                }
            }
            throw new IllegalArgumentException("No achievement found with name: " + name);
        }
    }
} 
