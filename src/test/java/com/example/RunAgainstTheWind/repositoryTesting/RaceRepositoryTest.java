package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.race.model.Race;
import com.example.RunAgainstTheWind.domain.race.repository.RaceRepository;

@SpringBootTest
@Transactional
public class RaceRepositoryTest {
    
    @Autowired
    private RaceRepository raceRepository;

    @BeforeEach
    public void setUp() {
        raceRepository.deleteAll();
    }

    @Test
    public void testCreateRace() {
        // Create new race
        LocalDate localDate = LocalDate.of(2025, 3, 24);
        Date date = Date.valueOf(localDate);
        Race race = new Race(date, "Montreal", 10.0);
        Race savedRace = raceRepository.save(race);

        // Check if it exists
        assertNotNull(race.getRaceId());

        // Check if it is retrievable from the database
        Optional<Race> retrievedRace = raceRepository.findById(savedRace.getRaceId());
        assertNotNull(retrievedRace);

        // Check if the attributes are correct
        assertNotNull(retrievedRace.get().getDate());
        assertNotNull(retrievedRace.get().getLocation());
        assertNotNull(retrievedRace.get().getDistance());
        assertEquals(date, retrievedRace.get().getDate());
        assertEquals("Montreal", retrievedRace.get().getLocation());   
        assertEquals(10.0, retrievedRace.get().getDistance());
    }
}
