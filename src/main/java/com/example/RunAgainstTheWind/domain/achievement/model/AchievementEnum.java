package com.example.RunAgainstTheWind.domain.achievement.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementEnum {
    FIRST_RUN(1, "First Run", "Completed your first run"),
    EARLY_BIRD(2, "Early Bird", "Completed a run before 6 AM"),
    NIGHT_OWL(3, "Night Owl", "Completed a run after 9 PM"),
    FIVE_KM(4, "5K Runner", "Completed a 5K run"),
    TEN_KM(5, "10K Runner", "Completed a 10K run"),
    HALF_MARATHON(6, "Half Marathon", "Completed a half marathon"),
    MARATHON(7, "Marathon", "Completed a full marathon");

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