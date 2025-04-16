package com.example.RunAgainstTheWind.domain.achievement.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AchievementService {

    private static final String FILE_PATH;
    private static final String DELIMITER = ",";

    static {
        // Not sure if this is correct for production
        String basePath = System.getProperty("user.dir") + "/src/main/java/com/example/RunAgainstTheWind/domain/achievement/";
        FILE_PATH = basePath + "Achievement.csv";
    }
    
    /**
     * Get all achieved achievements for a specific user
     * @param userId The user ID
     * @return List of achieved achievement names
     * @throws IOException If file cannot be read
     */
    public List<String> getUserAchievements(String userId) throws IOException {
        List<String> lines = readFile();
        List<String> achievedAchievements = new ArrayList<>();
        
        for (String line : lines) {
            String[] parts = line.trim().split(DELIMITER);
            if (parts.length >= 4 && parts[0].equals(userId)) {
                if ("1".equals(parts[1])) {
                    achievedAchievements.add("ACHIEVEMENT_1");
                }
                if ("1".equals(parts[2])) {
                    achievedAchievements.add("ACHIEVEMENT_2");
                }
                if ("1".equals(parts[3])) {
                    achievedAchievements.add("ACHIEVEMENT_3");
                }
                break;
            }
        }
        
        return achievedAchievements;
    }
    
    /**
     * Mark a specific achievement as achieved for a user
     * @param userId The user ID
     * @param achievementName The achievement name (ACHIEVEMENT_1, ACHIEVEMENT_2, or ACHIEVEMENT_3)
     * @return true if successfully updated, false otherwise
     * @throws IOException If file cannot be read or written
     * @throws IllegalArgumentException If achievement name is invalid
     */
    public boolean markAchievement(String userId, String achievementName) throws IOException {
        // Convert achievement name to index
        int achievementIndex;
        switch (achievementName.toUpperCase()) {
            case "ACHIEVEMENT_1":
                achievementIndex = 1;
                break;
            case "ACHIEVEMENT_2":
                achievementIndex = 2;
                break;
            case "ACHIEVEMENT_3":
                achievementIndex = 3;
                break;
            default:
                throw new IllegalArgumentException("Invalid achievement name: " + achievementName);
        }
        
        List<String> lines = readFile();
        boolean userFound = false;
        List<String> updatedLines = new ArrayList<>();
        
        for (String line : lines) {
            String[] parts = line.trim().split(DELIMITER);
            
            if (parts.length >= 4 && parts[0].equals(userId)) {
                userFound = true;
                parts[achievementIndex] = "1"; // Mark achievement as achieved
                updatedLines.add(String.join(DELIMITER, parts));
            } else {
                updatedLines.add(line);
            }
        }
        
        // If user wasn't found, add a new entry
        if (!userFound) {
            throw new IllegalArgumentException("User ID not found: " + userId);
        }
        
        // Write back to file
        writeFile(updatedLines);
        return true;
    }
    
    /**
     * Read all lines from the achievement file
     * @return List of lines from the file
     * @throws IOException If file cannot be read
     */
    private List<String> readFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
    
    /**
     * Write lines back to the achievement file
     * @param lines The lines to write
     * @throws IOException If file cannot be written
     */
    private void writeFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
