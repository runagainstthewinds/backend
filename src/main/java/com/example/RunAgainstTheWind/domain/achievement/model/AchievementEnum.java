package com.example.RunAgainstTheWind.domain.achievement.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementEnum {
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

    public static AchievementEnum fromId(int id) {
        for (AchievementEnum achievement : values()) {
            if (achievement.getId() == id) {
                return achievement;
            }
        }
        throw new IllegalArgumentException("No achievement found with id: " + id);
    }

    public static AchievementEnum fromName(String name) {
        for (AchievementEnum achievement : values()) {
            if (achievement.getName().equals(name)) {
                return achievement;
            }
        }
        throw new IllegalArgumentException("No achievement found with name: " + name);
    }
} 