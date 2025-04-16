package com.example.RunAgainstTheWind.domain.achievement.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user achievements stored in a CSV file.
 */
@Service
public class AchievementService {

    private static final String FILE_PATH;
    private static final String DELIMITER = ",";
    private static final String ACHIEVED = "1";
    private static final String NOT_ACHIEVED = "0";

    static {
        String basePath = System.getProperty("user.dir") + "/data/";
        FILE_PATH = basePath + "Achievement.csv";
    }

    /**
     * Retrieves all achieved achievements for a given user.
     *
     * @param userId the ID of the user
     * @return a list of achieved achievement names
     * @throws IOException if the achievement file cannot be read
     * @throws IllegalStateException if the file is empty or malformed
     */
    public List<String> getUserAchievements(String userId) throws IOException {
        List<String> lines = readFile();
        validateFileNotEmpty(lines);

        List<String> achievementNames = parseAchievementNames(lines.get(0));
        return findUserAchievements(lines, userId, achievementNames);
    }

    /**
     * Marks an achievement as achieved for a user.
     *
     * @param userId the ID of the user
     * @param achievementName the name of the achievement to mark
     * @return true if the achievement was successfully marked
     * @throws IOException if the file cannot be read or written
     * @throws IllegalArgumentException if the achievement name is invalid
     * @throws IllegalStateException if the file is empty
     */
    public boolean markAchievement(String userId, String achievementName) throws IOException {
        List<String> lines = readFile();
        validateFileNotEmpty(lines);

        String[] headerParts = lines.get(0).trim().split(DELIMITER);
        int achievementIndex = findAchievementIndex(headerParts, achievementName);

        List<String> updatedLines = updateOrAddUserAchievement(lines, userId, headerParts, achievementIndex);
        writeFile(updatedLines);
        return true;
    }

    /**
     * Reads all lines from the achievement CSV file.
     *
     * @return a list of lines from the file
     * @throws IOException if the file cannot be read
     */
    private List<String> readFile() throws IOException {
        return Files.readAllLines(Paths.get(FILE_PATH));
    }

    /**
     * Writes lines to the achievement CSV file.
     *
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    private void writeFile(List<String> lines) throws IOException {
        Files.write(Paths.get(FILE_PATH), lines);
    }

    /**
     * Validates that the file is not empty.
     *
     * @param lines the lines read from the file
     * @throws IllegalStateException if the file is empty
     */
    private void validateFileNotEmpty(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalStateException("Achievement file is empty");
        }
    }

    /**
     * Parses achievement names from the CSV header.
     *
     * @param header the header line of the CSV
     * @return a list of achievement names
     */
    private List<String> parseAchievementNames(String header) {
        return Arrays.stream(header.trim().split(DELIMITER))
                .skip(1) // Skip USER_ID column
                .collect(Collectors.toList());
    }

    /**
     * Finds the achievements for a specific user.
     *
     * @param lines the lines from the CSV file
     * @param userId the ID of the user
     * @param achievementNames the list of achievement names
     * @return a list of achieved achievement names
     */
    private List<String> findUserAchievements(List<String> lines, String userId, List<String> achievementNames) {
        List<String> achievedAchievements = new ArrayList<>();

        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.trim().split(DELIMITER);
            if (parts.length >= achievementNames.size() + 1 && parts[0].equals(userId)) {
                for (int i = 0; i < achievementNames.size(); i++) {
                    if (ACHIEVED.equals(parts[i + 1])) {
                        achievedAchievements.add(achievementNames.get(i));
                    }
                }
                break;
            }
        }
        return achievedAchievements;
    }

    /**
     * Finds the index of the specified achievement in the header.
     *
     * @param headerParts the header parts from the CSV
     * @param achievementName the name of the achievement
     * @return the index of the achievement
     * @throws IllegalArgumentException if the achievement name is invalid
     */
    private int findAchievementIndex(String[] headerParts, String achievementName) {
        for (int i = 1; i < headerParts.length; i++) {
            if (headerParts[i].equalsIgnoreCase(achievementName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid achievement name: " + achievementName);
    }

    /**
     * Updates an existing user's achievement or adds a new user row.
     *
     * @param lines the lines from the CSV file
     * @param userId the ID of the user
     * @param headerParts the header parts from the CSV
     * @param achievementIndex the index of the achievement to mark
     * @return the updated list of lines
     */
    private List<String> updateOrAddUserAchievement(List<String> lines, String userId, String[] headerParts, int achievementIndex) {
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add(lines.get(0)); // Preserve header
        boolean userFound = false;

        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.trim().split(DELIMITER);
            if (parts.length >= headerParts.length && parts[0].equals(userId)) {
                userFound = true;
                parts[achievementIndex] = ACHIEVED;
                updatedLines.add(String.join(DELIMITER, parts));
            } else {
                updatedLines.add(line);
            }
        }

        if (!userFound) {
            String[] newRow = new String[headerParts.length];
            newRow[0] = userId;
            Arrays.fill(newRow, 1, newRow.length, NOT_ACHIEVED);
            newRow[achievementIndex] = ACHIEVED;
            updatedLines.add(String.join(DELIMITER, newRow));
        }
        return updatedLines;
    }
}