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
        
        // Parse header to get achievement names
        String[] headerParts = lines.get(0).trim().split(DELIMITER);
        List<String> achievementNames = new ArrayList<>();
        
        // Start from index 1 to skip USER_ID column
        for (int i = 1; i < headerParts.length; i++) {
            achievementNames.add(headerParts[i]);
        }
        
        // Look for the user's achievements
        for (int i = 1; i < lines.size(); i++) {  
            String line = lines.get(i);
            String[] parts = line.trim().split(DELIMITER);
            
            if (parts.length >= achievementNames.size() + 1 && parts[0].equals(userId)) {
                // Check each achievement column
                for (int j = 0; j < achievementNames.size(); j++) {
                    if ("1".equals(parts[j + 1])) {  
                        achievedAchievements.add(achievementNames.get(j));
                    }
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
        List<String> lines = readFile();
        
        if (lines.isEmpty()) {
            throw new IOException("Achievement file is empty");
        }
        
        // Parse header to get achievement names and their indices
        String[] headerParts = lines.get(0).trim().split(DELIMITER);
        int achievementIndex = -1;
        
        // Find the index of the specified achievement
        for (int i = 1; i < headerParts.length; i++) {  // Start from 1 to skip USER_ID
            if (headerParts[i].equalsIgnoreCase(achievementName)) {
                achievementIndex = i;
                break;
            }
        }
        
        if (achievementIndex == -1) {
            throw new IllegalArgumentException("Invalid achievement name: " + achievementName);
        }
        
        boolean userFound = false;
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add(lines.get(0));  // Add header row
        
        for (int i = 1; i < lines.size(); i++) {  // Start from 1 to skip header
            String line = lines.get(i);
            String[] parts = line.trim().split(DELIMITER);
            
            if (parts.length >= headerParts.length && parts[0].equals(userId)) {
                userFound = true;
                parts[achievementIndex] = "1";  // Mark achievement as achieved
                updatedLines.add(String.join(DELIMITER, parts));
            } else {
                updatedLines.add(line);
            }
        }
        
        if (!userFound) {
            String[] newRow = new String[headerParts.length];
            newRow[0] = userId;  // Set USER_ID
            for (int i = 1; i < headerParts.length; i++) {
                newRow[i] = (i == achievementIndex) ? "1" : "0";  
            }
            updatedLines.add(String.join(DELIMITER, newRow));
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
