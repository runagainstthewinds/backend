package com.example.RunAgainstTheWind.enumeration;

public enum Difficulty {
    EASY(2), MEDIUM(4), HARD(6);

    private final int sessionsPerWeek;

    Difficulty(int sessionsPerWeek) {
        this.sessionsPerWeek = sessionsPerWeek;
    }

    public int getSessionsPerWeek() {
        return sessionsPerWeek;
    }
}
